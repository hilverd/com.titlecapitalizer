(ns com.titlecapitalizer.model.capitalizers.capitalizer
  "Capitalize titles according to user-customizable styles."
  (:require [clojure.string :as string]
            [com.titlecapitalizer.model.string-utils :as string-utils]
            [com.titlecapitalizer.model.tokenizers.tokenizer :as tokenizer]
            [com.titlecapitalizer.model.styles.style :as style]
            [com.titlecapitalizer.model.pos-taggers.pos-tagger :as pos-tagger]))

(defn capitalize-all-hy-comp-words
  "Given a hyphenated compound such \"merry-go-round\" or \"well-being\",
   capitalize every word in the compound."
  [x]
  {:pre [(string? x)]
   :post [(string? %)]}
  (string/replace
   (string-utils/capitalize-first-letter x)
   #"-[a-z]"
   #(str "-" (string/capitalize (subs % 1)))))

(defn change-token-case-in-title-wrt-hy-comp-style
  "Given a title and the result of POS tagging it,
   capitalize or make lower case the kth token in the title w.r.t. the given
   hyphenated compound style."
  [capitalize hy-comp-style pos-tagged-title title k]
  {:pre [(tokenizer/hy-comp-style? hy-comp-style)
         (tokenizer/title? title)
         (pos-tagger/pos-tagged-title? pos-tagged-title)
         (integer? k)]
   :post [(tokenizer/title? %)]}
  (let [pos-tagged-token (nth pos-tagged-title k)
        [token _ index] pos-tagged-token
        before (subs title 0 index)
        after (subs title (+ index (count token)))
        changed-token
        (if capitalize
          (case hy-comp-style
            :all (capitalize-all-hy-comp-words token)
            (string-utils/capitalize-first-letter token))
          (string-utils/lower-case-first-letter token))]
    (str before changed-token after)))

(def capitalize-token-in-title-wrt-hy-comp-style
  (partial change-token-case-in-title-wrt-hy-comp-style true))

(def lower-case-token-in-title-wrt-hy-comp-style
  (partial change-token-case-in-title-wrt-hy-comp-style false))

(defn replace-token-in-title
  "Given a title and the result of POS tagging it, replace the kth token in the
   title by new-token. This should only be done if new-token is a capitalization
   variant of the kth token of the title (thereby guaranteeing that token
   lengths are not changed). That is, new-token is obtained from the kth token
   of the title by capitalizing certain letters."
  [title pos-tagged-title k new-token]
  {:pre [(tokenizer/title? title)
         (pos-tagger/pos-tagged-title? pos-tagged-title)
         (integer? k)
         (tokenizer/token? new-token)]
   :post [(tokenizer/title? %)]}
  (let [pos-tagged-token (nth pos-tagged-title k)
        [token _ index] pos-tagged-token
        before (subs title 0 index)
        after (subs title (+ index (count token)))]
    (assert (style/capitalization-variant? new-token token))
    (str before new-token after)))

(defn condition-matches?
  "Test whether condition matches the kth element of pos-tagged-title."
  [pos-tagged-title k condition]
  {:pre [(pos-tagger/pos-tagged-title? pos-tagged-title)
         (integer? k)
         (style/condition? condition)]}
  (let [property (:property condition)
        operator (:operator condition)
        value (:value condition)
        [token tag _] (nth pos-tagged-title k)]
    (case property
      :pos-tag ((if (= operator :is) = not=) tag value)
      :token ((if (= operator :is) = not=) token value)
      :token-length ((if (= operator :greater-than) > <) (count token) value)
      :token-position ((if (= operator :is) = not=)
                       k
                       (if (= value :first) 0 (- (count pos-tagged-title) 1)))
      :prev-token (if (pos? k)
                    (condition-matches?
                     pos-tagged-title
                     (- k 1)
                     {:operator operator
                      :property :token
                      :value value})))))

(defn rule-matches?
  "Test whether rule matches the kth element of pos-tagged-title."
  [pos-tagged-title k rule]
  {:pre [(pos-tagger/pos-tagged-title? pos-tagged-title)
         (integer? k)
         (style/rule? rule)]}
  (every? true? (map #(condition-matches? pos-tagged-title k %)
                     (:conditions rule))))

(defn capitalize-token-in-title-using-style
  "Capitalize the kth token in title w.r.t. pos-tagged-title using style."
  [style pos-tagged-title title k]
  {:pre [(style/style? style)
         (tokenizer/title? title)
         (integer? k)]
   :post [(tokenizer/title? %)]}
  (let [rules (:rules style)
        hy-comp-style (style/hy-comp-style-for-style style)
        first-matching-rule (first (filter #(rule-matches? pos-tagged-title k %)
                                           rules))]
    (if first-matching-rule
      (let [action (:action first-matching-rule)
            [token tag _] (nth pos-tagged-title k)
            token-is-lowercase (not (re-find #"[A-Z]" token))]
        (cond
         (tokenizer/token? action) (replace-token-in-title
                                    title pos-tagged-title k action)
         (or (= action :capitalize)
             (and (= action :capitalize-if-lowercase) token-is-lowercase))
         (capitalize-token-in-title-wrt-hy-comp-style
          hy-comp-style pos-tagged-title title k)
         :else title))
      title)))

(defn capitalize-pos-tagged-title-using-style
  "Capitalize title w.r.t. pos-tagged-title using style."
  [style title pos-tagged-title]
  {:pre [(style/style? style)
         (tokenizer/title? title)
         (pos-tagger/pos-tagged-title? pos-tagged-title)]
   :post [(tokenizer/title? %)]}
  (let [n (count pos-tagged-title)]
    (reduce (partial capitalize-token-in-title-using-style style
                     pos-tagged-title)
            title
            (range n))))

(defn lower-case-pos-tagged-title-using-style
  [style title pos-tagged-title]
  (let [hy-comp-style (style/hy-comp-style-for-style style)
        n (count pos-tagged-title)]
    (reduce
     (partial lower-case-token-in-title-wrt-hy-comp-style hy-comp-style
              pos-tagged-title)
     title
     (range n))))

(defn capitalize-title
  "Tokenize title using tokenizer, then POS-tag using pos-tagger-model, then
   capitalize the result using style."
  [tokenizer pos-tagger-model style title]
  {:pre [(style/style? style)
         (tokenizer/title? title)]
   :post [(tokenizer/title? %)]}
  (let [hy-comp-style (style/hy-comp-style-for-style style)
        tokenized-title (tokenizer/tokenize-title tokenizer hy-comp-style title)
        pos-tagged-title (pos-tagger/pos-tag-tokenized-title pos-tagger-model
                                                             tokenized-title)]
    (capitalize-pos-tagged-title-using-style style title pos-tagged-title)))

(defn lower-case-title
  "Tokenize title using tokenizer, then POS-tag using pos-tagger-model, then
   lower-case the result using style."
  [tokenizer pos-tagger-model style title]
  {:pre [(style/style? style)
         (tokenizer/title? title)]
   :post [(tokenizer/title? %)]}
  (let [hy-comp-style (style/hy-comp-style-for-style style)
        tokenized-title (tokenizer/tokenize-title tokenizer hy-comp-style title)
        pos-tagged-title (pos-tagger/pos-tag-tokenized-title pos-tagger-model
                                                             tokenized-title)]
    (lower-case-pos-tagged-title-using-style style title pos-tagged-title)))
