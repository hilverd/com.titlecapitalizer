(ns com.titlecapitalizer.model.tokenizers.predefined
  (:require [com.titlecapitalizer.model.styles.style :as style]
            [com.titlecapitalizer.model.styles.predefined :as predefined-styles]
            [com.titlecapitalizer.model.tokenizers.tokenizer :as tokenizer]
            [opennlp.nlp :as nlp]))

(def opennlp-en-tokenizer (nlp/make-tokenizer "resources/opennlp/en-token.bin"))

(defn tokenize-title-using-predefined-style
  [style-name title]
  (let [style (predefined-styles/style style-name)
        hy-comp-style (style/hy-comp-style-for-style style)]
    (tokenizer/tokenize-title opennlp-en-tokenizer hy-comp-style title)))
