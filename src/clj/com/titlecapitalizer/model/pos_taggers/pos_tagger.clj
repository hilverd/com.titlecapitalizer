(ns com.titlecapitalizer.model.pos-taggers.pos-tagger
  "POS-tag a title using Apache OpenNLP's POS tagger."
  (:require [com.titlecapitalizer.model.tokenizers.tokenizer :as tokenizer]
            [com.titlecapitalizer.model.styles.style :as style]))

(defn pos-tagged-token?
  "Return true if x is a POS-tagged (indexed) token, i.e. a triple (w, t, j),
   where w is a token, t is a part-of-speech tag, and j is a nonnegative
   integer."
  [x]
  (and
   (vector? x)
   (= (count x) 3)
   (let [[token pos-tag index] x]
     (and (tokenizer/token? token) (style/pos-tag? pos-tag)
          (integer? index)))))

(defn pos-tagged-title?
  "Return true if x is a POS-tagged title, i.e. a non-empty sequence of
   POS-tagged tokens."
  [x]
  (and
   (vector? x)
   (seq x)
   (every? pos-tagged-token? x)))

(defn pos-tag-tokenized-title
  "Assign POS tags to tokenized-title using pos-tagger-model."
  [pos-tagger-model tokenized-title]
  {:pre [(tokenizer/tokenized-title? tokenized-title)]
   :post [(pos-tagged-title? %)]}
  (let [tokens (mapv first tokenized-title)
        indices (mapv second tokenized-title)
        pos-tagged-title (pos-tagger-model tokens)]
    (mapv #(let [[[token pos-tag-string] index] [%1 %2]]
             (vector token pos-tag-string index))
          pos-tagged-title
          indices)))

(defn pos-tag-title
  "Tokenize title with respect to hy-comp-style using tokenizer and then POS-tag
   it using pos-tagger-model."
  [tokenizer hy-comp-style pos-tagger-model title]
  {:pre [(tokenizer/title? title)]
   :post [(pos-tagged-title? %)]}
  (let [tokenized-title
        (tokenizer/tokenize-title tokenizer hy-comp-style title)]
    (pos-tag-tokenized-title pos-tagger-model tokenized-title)))
