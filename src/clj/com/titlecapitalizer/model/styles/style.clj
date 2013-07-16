(ns com.titlecapitalizer.model.styles.style
  "Styles for capitalizing titles according to rules."
  (:require [clojure.string :as string]))

(def condition-operators #{:is :is-not :greater-than :less-than})

(defn condition-operator?
  "Return true if x is a relational operator as used in conditions of
   capitalization rules."
  [x]
  (contains? condition-operators x))

(def condition-properties
  #{:pos-tag :token :token-length :token-position :prev-token})

(defn condition-property?
  "Return true if x is a property as used in conditions of capitalization
   rules."
  [x]
  (contains? condition-properties x))

(def positions #{:first :last})

(defn position?
  "Return true if x is a position (first/last) as used in conditions of
   capitalization rules."
  [x]
  (contains? positions x))

(defn pos-tag?
  "Return true if x is a POS tag."
  [x]
  (string? x))

(def pos-integer? #(and (pos? %) (integer? %)))

(defn token?
  "A token is a string, typically representing a word, punctuation, or number."
  [x]
  (string? x))

(def hy-comp-styles #{:first :all :separate})

(defn hy-comp-style?
  "Return true if x is a hyphenated compound style, i.e. a keyword specifying
   which approach to take when capitalizing hyphenated compounds: (1) capitalize
   only the first word, (2) capitalize all words, or (3) capitalize the
   constituent words separately, as if they were not joined together (but leave
   the hyphens in place)."
  [x]
  (contains? hy-comp-styles x))

(defn condition-value?
  "Return true if x a is a value as used in conditions of capitalization rules."
  [x]
  (or (position? x) (pos-tag? x) (token? x) (pos-integer? x)))

(defn condition?
  "Return true if x is a condition as used in capitalization rules."
  [x]
  (and
   (map? x)
   (condition-operator? (:operator x))
   (condition-property? (:property x))
   (condition-value? (:value x))))

(defn rule-conditions?
  "Return true if x is a vector of conditions as used in capitalization rules."
  [x]
  (and (vector? x) (every? condition? x)))

(defn rule-action?
  "Return true if x is an action as used in capitalization rules."
  [x]
  (or (#{:capitalize :capitalize-if-lowercase :no-capitalize} x)
       (token? x)))

(defn capitalization-variant?
  "Return true if x is a capitalization variant of y. That is, x is obtained
   from y by capitalizing certain letters."
  [x y]
  (and
   (= (count x) (count y))
   (every? #(let [[xc yc] %
                  yc-uppercase (first (string/capitalize yc))]
              (or (= xc yc) (= xc yc-uppercase)))
           (zipmap x y))))

(defn rule?
  "Return true if x is a capitalization rule."
  [x]
  (and
   (map? x)
   (let [conditions (:conditions x)
         action (:action x)]
     (and
      (rule-conditions? conditions)
      (rule-action? action)
      ;; If the action is a token, then the rule must be of the form (if (is
      ;; token <token1>) <token2>), where <token2> is a capitalization variant
      ;; of <token1>.
      (if (token? action)
        (and
         (= (count conditions) 1)
         (let [condition (first conditions)
               operator (:operator condition)
               property (:property condition)
               value (:value condition)]
           (and
            (= operator :is)
            (= property :token)
            (capitalization-variant? action value))))
        true)))))

(defn version?
  "Return true if x is a string representing a version number as used in
   capitalization styles."
  [x]
  (string? x))

(defn option?
  "Return true if x is an option as used in capitalization styles, i.e. a
   version specification or a hyphenated compound style specification."
  [x]
  (and
   (map? x)
   (= (count x) 1)
   (let [[x-key x-value] (first (seq x))]
     (or (and (= x-key :version) (version? x-value))
         (and (= x-key :hy-comp-style) (hy-comp-style? x-value))))))

(defn options?
  "Return true if x is a map representing a set of options as used in
   capitalization styles, i.e. a map from keywords (:version, :hy-comp-style) to
   values."
  [x]
  (and
   (map? x)
   (let [options (map #(into {} [%]) (seq x))]
     (every? option? options))))

(defn style?
  "Return true if x is a capitalization style."
  [x]
  (and (map? x) (options? (:options x)) (every? rule? (:rules x))))

(defn hy-comp-style-for-style
  "Given a capitalization style, return its hyphenated compound style
   specification, or :separate if none exists."
  [style]
  {:pre [(style? style)]
   :post [(hy-comp-style? %)]}
  (let [options (:options style)]
    (or (:hy-comp-style options) :separate)))
