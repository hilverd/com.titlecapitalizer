(ns com.titlecapitalizer.main.render
  (:use-macros [crate.def-macros :only [defpartial]])
  (:require [clojure.string :as string]
            [crate.core :as crate]
            [crate.util :refer [escape-html]]
            [com.titlecapitalizer.model.string-utils :as string-utils]))

(defn html
  [x]
  (.-outerHTML (crate/html x)))

;; Map from Penn Treebank Project tags, i.e. symbols representing parts of
;; speech, to descriptions.
(def treebank-tags
  {"#"     "hash / pound / number sign"
   "$"     "dollar"
   "''"    "closing quotation mark"
   ","     "comma"
   "-LRB-" "opening parenthesis"
   "-RRB-" "closing parenthesis"
   "."     "sentence terminator"
   ":"     "colon or ellipsis"
   "CC"    "conjunction, coordinating"
   "CD"    "numeral, cardinal"
   "DT"    "determiner"
   "EX"    "existential there"
   "FW"    "foreign word"
   "IN"    "preposition or conjunction, subordinating"
   "JJ"    "adjective or numeral, ordinal"
   "JJR"   "adjective, comparative"
   "JJS"   "adjective, superlative"
   "LS"    "list item marker"
   "MD"    "modal auxiliary"
   "NN"    "noun, common, singular or mass"
   "NNP"   "noun, proper, singular"
   "NNPS"  "noun, proper, plural"
   "NNS"   "noun, common, plural"
   "PDT"   "pre-determiner"
   "POS"   "genitive marker"
   "PRP"   "pronoun, personal"
   "PRP$"  "pronoun, possessive"
   "RB"    "adverb"
   "RBR"   "adverb, comparative"
   "RBS"   "adverb, superlative"
   "RP"    "particle"
   "SYM"   "symbol"
   "TO"    "'to' as preposition or infinitive marker"
   "UH"    "interjection"
   "VB"    "verb, base form"
   "VBD"   "verb, past tense"
   "VBG"   "verb, present participle or gerund"
   "VBN"   "verb, past participle"
   "VBP"   "verb, present tense, not 3rd person singular"
   "VBZ"   "verb, present tense, 3rd person singular"
   "WDT"   "WH-determiner"
   "WP"    "WH-pronoun"
   "WP$"   "WH-pronoun, possessive"
   "WRB"   "WH-adverb"
   "``"    "opening quotation mark"})

(defn treebank-tag-with-description
  [treebank-tag-string]
  (let [description (treebank-tags treebank-tag-string)]
    (str treebank-tag-string " "
         (string-utils/enclose-in-parentheses description))))

(defn treebank-tag-typeahead-input-html
  [token-index]
  (let [id-for-token (str "pos-tag-" token-index)]
    (html [:input.span2 {:type "text"
                         :id id-for-token
                         :placeholder "Enter new tag here"
                         :data-provide "typeahead"}])))

(defn symbolic-pos-tagged-token?
  [pos-tagged-token]
  (let [[token _ _] pos-tagged-token]
    (boolean (re-find #"[a-zA-Z]" token))))

(defn pos-tagged-token-html
  ([token-index pos-tagged-token]
     (pos-tagged-token-html false token-index pos-tagged-token))
  ([mark-as-changed token-index pos-tagged-token]
     (let [[token pos-tag _] pos-tagged-token
           tag-with-description (treebank-tag-with-description pos-tag)
           symbolic-token (symbolic-pos-tagged-token? pos-tagged-token)
           typeahead-input (if symbolic-token
                             (treebank-tag-typeahead-input-html token-index)
                             "Not editable.")
           label-colour (if symbolic-token
                          (if mark-as-changed "label-important" "label-info")
                          "")]
       (html [:span.pos-tagged-token {:id (str "pos-tagged-token-" token-index)}
              (escape-html token)
              " "
              [:a {:href "#"
                   :rel "clickover"
                   :class (string/join " " ["pos-tag" "label" label-colour])
                   :data-content typeahead-input
                   :data-original-title tag-with-description
                   :data-placement "bottom"}
               [:abbr.pos-tag-abbr {:title (treebank-tags pos-tag)}
                pos-tag]]]))))

(defn pos-tagged-title-html
  [pos-tagged-title]
  (string/join
   " "
   (map-indexed pos-tagged-token-html pos-tagged-title)))

(defn select-content
  [select-class options selected-option]
  "options is a vector of vectors [option, description] where option is a
   keyword and description a string, and selected-option is a keyword."
  (letfn [(option-content [[option description]]
            [:option (into {:value (name option)}
                           (when (= option selected-option) {:selected "true"}))
             description])]
    [:select {:class (string/join " " [select-class "rule-control"])
              :disabled "disabled"}
     (map option-content options)]))

(def rule-condition-property-select-content
  (partial select-content
           "rule-condition-property"
           [[:pos-tag "POS tag"]
            [:token "token"]
            [:token-length "token length"]
            [:token-position "token position"]
            [:prev-token "previous token"]]))

(def equality-relational-operator-select-content
  (partial select-content
           "eq-rel-op"
           [[:is "="]
            [:is-not "≠"]]))

(def numerical-relational-operator-select-content
  (partial select-content
           "num-rel-op"
           [[:greater-than ">"]
            [:less-than "<"]]))

(def position-select-content
  (partial select-content
           "token-position"
           [[:first "first"]
            [:last "last"]]))

(def rule-action-select-content
  (partial select-content
           "rule-action"
           [[:capitalize "capitalize"]
            [:capitalize-if-lowercase "capitalize if lowercase"]
            [:no-capitalize "do not capitalize"]
            [:replace "replace by"]]))

(defn rule-number-content
  [index]
  (list
   [:span.cap-rule-label
    (str "Rule " (inc index))]))

(defn pos-tag-typeahead-content
  [pos-tag]
  (let [tag-with-description (treebank-tag-with-description pos-tag)]
    [:a.btn {:href "#"
             :rel "clickover"
             :data-content ""
             :data-original-title tag-with-description
             :data-placement "bottom"}
     [:span.pos-tag-btn pos-tag]
     [:span.caret]]))

(defn token-field-content
  [token]
  [:input {:type "text"
           :class (string/join " " ["token-content" "rule-control"])
           :value token
           :disabled "disabled"}])

(defn token-length-field-content
  [length]
  [:input {:type "text"
           :class (string/join " " ["token-length" "rule-control"])
           :value length
           :onfocus "this.select()"
           :onmouseup "return false"
           :disabled "disabled"}])

(defn rule-condition-content
  [num-conditions condition-index rule-index condition]
  (let [is-first-condition (zero? condition-index)
        is-last-condition (= condition-index (dec num-conditions))
        several-conditions (> num-conditions 1)
        operator (:operator condition)
        property (:property condition)
        value (:value condition)]
    [:div.rule-condition-or-action
     [:div.rule-conjunction (if is-first-condition "If" "and")]
     [:div.rule-body
      (rule-condition-property-select-content property)
      (case property
        (:pos-tag :token :token-position :prev-token)
        (equality-relational-operator-select-content operator)
        (:token-length)
        (numerical-relational-operator-select-content operator))
      (case property
        :pos-tag (pos-tag-typeahead-content value)
        (:token :prev-token) (token-field-content value)
        :token-length (token-length-field-content value)
        :token-position (position-select-content value))]]))

(defn rule-action-content
  [action]
  (let [selected-action (if (keyword? action) action :replace)]
    [:div.rule-condition-or-action
     [:div.rule-conjunction "then"]
     [:div.rule-body
      (rule-action-select-content selected-action)
      (when (= selected-action :replace)
        (token-field-content action))]]))

(defn rule-content
  [index rule]
  (let [conditions (:conditions rule)
        action (:action rule)
        num-conditions (count conditions)]
    [:div.cap-rule
     (rule-number-content index)
     (map-indexed #(rule-condition-content num-conditions %1 index %2)
                  conditions)
     (rule-action-content action)]))

(defn hy-comp-style-content
  [hy-comp-style]
  (let [style-description {:first "first word only"
                           :separate "separate"
                           :all "all"}]
    (letfn [(style-radio-content [style]
              [:label.radio.inline
               [:input.hy-comp-style-radio
                (into {:type "radio"
                       :name "optionsRadios"
                       :value (name style)
                       :disabled "disabled"}
                      (when (= style hy-comp-style) {:checked "true"}))]
               (style-description style)])]
      [:div.control-group
       [:label.control-label {:for "hyCompStyle"}
        "Hyphenated compounds:"]
       [:div#hyCompStyle.controls
        (map style-radio-content [:first :separate :all])]])))

(defn style-content
  [style hy-comp-style]
  (let [rules (:rules style)]
    [:div#capitalizationStyleDetails.controls
     (hy-comp-style-content hy-comp-style)
     [:div#sortable.style-rules (map-indexed rule-content rules)]]))

(defn style-content-html
  [style hy-comp-style]
  (html (style-content style hy-comp-style)))
