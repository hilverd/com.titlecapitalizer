(ns com.titlecapitalizer.model.capitalizers.predefined
  (:require [com.titlecapitalizer.model.tokenizers.predefined
             :refer [opennlp-en-tokenizer]]
            [com.titlecapitalizer.model.pos-taggers.predefined
             :refer [en-pos-maxent-tagger-model]]
            [com.titlecapitalizer.model.styles.predefined
             :refer [style-name? predefined-styles]]
            [com.titlecapitalizer.model.capitalizers.capitalizer
             :as capitalizer]))

(defn lower-case-pos-tagged-title-using-predefined-style
  [style-name title pos-tagged-title]
  {:pre [(style-name? style-name)]}
  (let [style (predefined-styles style-name)]
    (capitalizer/lower-case-pos-tagged-title-using-style style title
                                                         pos-tagged-title)))

(defn lower-case-title-using-predefined-style
  [style-name title]
  (let [style (predefined-styles style-name)]
    (capitalizer/lower-case-title opennlp-en-tokenizer
                                  en-pos-maxent-tagger-model style title)))

(defn capitalize-pos-tagged-title-using-predefined-style
  [style-name title pos-tagged-title]
  {:pre [(style-name? style-name)]}
  (let [style (predefined-styles style-name)]
    (capitalizer/capitalize-pos-tagged-title-using-style style title
                                                         pos-tagged-title)))

(defn capitalize-title-using-predefined-style
  [style-name title]
  {:pre [(style-name? style-name)]}
  (let [style (predefined-styles style-name)]
    (capitalizer/capitalize-title opennlp-en-tokenizer
                                  en-pos-maxent-tagger-model style title)))
