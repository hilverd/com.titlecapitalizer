(ns com.titlecapitalizer.model.tokenizers.tokenizer
  "Tokenize titles with help from Apache OpenNLP's tokenizer."
  (:require [clojure.string :as string]
            [opennlp.nlp :as nlp]
            [com.titlecapitalizer.model.styles.style :as style]))

(def token? style/token?)

(defn indexed-token?
  "An indexed token is a pair (w, j), where w is a token and j an integer. This
   is used to keep track of where in a title each token is taken from."
  [x]
  (and
   (vector? x)
   (= (count x) 2)
   (token? (first x))
   (integer? (second x))))

(defn indexed-token-vector?
  [x]
  (and (vector? x) (every? indexed-token? x)))

(defn tokenized-title?
  "A tokenized title is a non-empty sequence (w_1, j_1), ..., (w_n, j_n) of
   indexed tokens, obtained by tokenizing a title S, such that for every i in
   {1, ..., n} we have that S contains the word w_i starting at position j_i."
  [x]
  (indexed-token-vector? x))

(defn hyphenated-compound-token?
  "A hyphenated compound token is an indexed token consisting of two or more
   words connected by hyphens, like \"merry-go-round\" or \"well-being\"."
  [indexed-token]
  {:pre [(indexed-token? indexed-token)]}
  (let [[token _] indexed-token]
    (re-find #"[a-zA-Z]-[a-zA-Z]" token)))

(defn split-hyphenated-compound-token
  "Split indexed-token up into a vector of indexed tokens, based on hyphens
   appearing in the token. For example, for [\"word-of-mouth\" 3] the result is
   [[\"word\" 3] [\"of\" 8] [\"mouth\" 11]]."
  [indexed-token]
  {:pre [(indexed-token? indexed-token)]
   :post [(indexed-token-vector? %)]}
  (let [[token index] indexed-token
        subtokens (string/split token #"-")
        subindices (->> subtokens (map count) (reductions #(+ 1 %1 %2) index)
                        vec)]
    (mapv vector subtokens subindices)))

(defn string-vector?
  "Return true if x is a vector of strings."
  [x]
  (and (vector? x) (every? string? x)))

(defn tokenized-title-from-opennlp-tokenized-string
  "Take a vector x of strings, as obtained by OpenNLP's tokenizer, and assign
   the appropriate indices explicitly, obtaining a vector of indexed tokens."
  [x]
  {:pre [(string-vector? x)]
   :post [(tokenized-title? %)]}
  (let [indices (map :start (:spans (meta x)))]
    (mapv vector x indices)))

(def title? string?)

(def hy-comp-styles style/hy-comp-styles)

(def hy-comp-style? style/hy-comp-style?)

(defn tokenize-title
  "Tokenize title with respect to hy-comp-style using tokenizer, that is, apply
   OpenNLP's tokenizer, turn the result into a tokenized title, and then split
   up any hyphenated compounds if required."
  [tokenizer hy-comp-style title]
  {:pre [(hy-comp-style? hy-comp-style)
         (title? title)]
   :post [(tokenized-title? %)]}
  (let [opennlp-tokenized-title (tokenizer title)
        tokenized-title (tokenized-title-from-opennlp-tokenized-string
                         opennlp-tokenized-title)]
    (case hy-comp-style
      :separate (vec (mapcat #(if (hyphenated-compound-token? %)
                                (split-hyphenated-compound-token %)
                                [%])
                             tokenized-title))
      tokenized-title)))
