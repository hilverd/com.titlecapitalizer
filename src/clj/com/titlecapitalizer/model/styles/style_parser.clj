(ns com.titlecapitalizer.model.styles.style-parser
  "Parse Clojure data structures representing capitalization styles and convert
   them to styles. These data structures should be obtained using the Clojure
   reader, by parsing expressions constructed using the following BNF grammar.

   <style>            ::= <option>* <rule>*
   <option>           ::= :version <version>
                        | :hy-comp-style <hy-comp-style>
   <hy-comp-style>    ::= first | separate | all
   <rule>             ::= (if (<condition>*) <action>)
   <condition>        ::= (<eq-rel-op> pos-tag <pos-tag>)
                        | (<eq-rel-op> token <token>)
                        | (<num-rel-op> token-length <number>)
                        | (<eq-rel-op> token-position <position>)
                        | (<eq-rel-op> prev-token <token>)
   <eq-rel-op>        ::= is | is-not
   <num-rel-op>       ::= greater-than | less-than
   <position>         ::= first | last
   <action>           ::= capitalize
                        | capitalize-if-lowercase
                        | no-capitalize
                        | <token>"
  (:require [com.titlecapitalizer.model.styles.style :as style]
            [com.titlecapitalizer.model.tokenizers.tokenizer :as tokenizer]
            [clojure.java.io :as io])
  (:import [java.io PushbackReader]))

;; TODO: add more helpful error reporting.

(defn parse-terminal
  "Test whether the symbol x is an allowed value, i.e. a member of the set
   allowed-values, and return it as a keyword. Both x and the elements in
   allowed-values are converted to keywords first."
  [allowed-values x]
  {:pre [(set? allowed-values)
         (symbol? x)]
   :post [(keyword? %)]}
  (let [x-keyword (keyword x)
        allowed-keywords (set (map keyword allowed-values))]
    (assert (contains? allowed-keywords x-keyword))
    x-keyword))

(def parse-condition-property #(parse-terminal style/condition-properties %))

(def parse-eq-rel-op #(parse-terminal #{:is :is-not} %))

(def parse-num-rel-op #(parse-terminal #{:greater-than :less-than} %))

(def parse-position #(parse-terminal style/positions %))

(defn parse-pos-tag
  [x]
  {:pre [(and (symbol? x) (style/pos-tag? (name x)))]}
  (str x))

(defn parse-token [x] {:pre [(tokenizer/token? x)]} x)

(defn parse-pos-integer [x] {:pre [(style/pos-integer? x)]} x)

(defn parse-condition
  [x]
  {:pre [(seq? x) (= (count x) 3)]}
  (let [[operator-sym property-sym value] x
        property (parse-condition-property property-sym)
        [parse-operator parse-value] (case property
                                       :pos-tag [parse-eq-rel-op parse-pos-tag]
                                       :token [parse-eq-rel-op parse-token]
                                       :token-length [parse-num-rel-op
                                                      parse-pos-integer]
                                       :token-position [parse-eq-rel-op
                                                        parse-position]
                                       :prev-token [parse-eq-rel-op
                                                    parse-token])]
    {:operator (parse-operator operator-sym)
     :property property
     :value (parse-value value)}))

(defn parse-rule-action
  [x]
  {:pre [(or (symbol? x) (tokenizer/token? x))]}
  (if (symbol? x)
    (parse-terminal #{:capitalize :capitalize-if-lowercase :no-capitalize} x)
    x))

(defn parse-rule
  [x]
  {:pre [(seq? x) (= (count x) 3) (= (first x) 'if)
         (seq? (second x))]}
  (let [[_ conditions action-sym] x]
    {:conditions (mapv parse-condition conditions)
     :action (parse-rule-action action-sym)}))

(def parse-hy-comp-style #(parse-terminal tokenizer/hy-comp-styles %))

(defn parse-option
  [x]
  {:pre [(seq? x)
         (= (count x) 2)
         (let [[option-key option-value] x]
           (or (and (= option-key :version) (string? option-value))
               (and (= option-key :hy-comp-style) (symbol? option-value))))]}
  (let [[option-key option-value] x]
    (case option-key
      :version {option-key option-value}
      :hy-comp-style {option-key (parse-hy-comp-style option-value)})))

(defn parse-options
  [x]
  {:pre [(seq? x)
         (even? (count x))]}
  (->> x (partition 2) (map parse-option) (apply merge)))

(defn parse-style
  "Given a Clojure expression x as read from a file f representing a
   capitalization style, convert x into the corresponding style."
  [x]
  {:pre [(seq? x)
         (> (count x) 0)
         (= (first x) 'capitalization-style)]
   :post [(style/style? %)]}
  (let [body (rest x)
        options (take-while #(not (seq? %)) body)
        rules (drop-while #(not (seq? %)) body)]
    {:options (parse-options options)
     :rules (mapv parse-rule rules)}))

(defn read-style-expr-from-file
  "Read in a Clojure expression from file f representing a capitalization style."
  [f]
  (binding [*read-eval* false]
    (with-open
        [r (io/reader f)]
      (read (PushbackReader. r)))))

(defn parse-style-from-file
  "Given a file f representing a capitalization style, return the corresponding
   style."
  [f]
  (parse-style (read-style-expr-from-file f)))
