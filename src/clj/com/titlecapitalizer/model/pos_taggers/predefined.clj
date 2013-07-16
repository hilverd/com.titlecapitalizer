(ns com.titlecapitalizer.model.pos-taggers.predefined
  (:require [com.titlecapitalizer.model.tokenizers.tokenizer :as tokenizer]
            [com.titlecapitalizer.model.tokenizers.predefined
             :as predefined-tokenizers]
            [com.titlecapitalizer.model.pos-taggers.pos-tagger :as pos-tagger]
            [com.titlecapitalizer.model.styles.style-parser :as style-parser]
            [com.titlecapitalizer.model.styles.style :as style]
            [com.titlecapitalizer.model.styles.predefined :as predefined-styles]
            [com.titlecapitalizer.model.capitalizers.capitalizer
             :as capitalizer]
            [opennlp.nlp :as nlp]))

(def tagger-model-names #{:en-pos-maxent :en-pos-perceptron})

(def tagger-model-dir "resources/opennlp")

(defn tagger-model-file
  [tagger-model-name]
  (str tagger-model-dir "/" (name tagger-model-name) ".bin"))

(defn tagger-model
  [tagger-model-name]
  (let [tagger-model-file (tagger-model-file tagger-model-name)]
    (nlp/make-pos-tagger tagger-model-file)))

(def en-pos-maxent-tagger-model (tagger-model :en-pos-maxent))

(defn pos-tag-tokenized-title-using-predefined-style
  [tokenized-title]
  (pos-tagger/pos-tag-tokenized-title en-pos-maxent-tagger-model
                                      tokenized-title))

(defn pos-tag-title-using-predefined-style
  [style-name title]
  (let [style (predefined-styles/style style-name)
        hy-comp-style (style/hy-comp-style-for-style style)]
    (pos-tagger/pos-tag-title predefined-tokenizers/opennlp-en-tokenizer
                              hy-comp-style en-pos-maxent-tagger-model title)))
