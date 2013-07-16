(ns com.titlecapitalizer.presenters.main
  (:require [shoreleave.middleware.rpc :refer [defremote]]
            [com.titlecapitalizer.model.global-setup] ;; load this first
            [com.titlecapitalizer.model.tokenizers.predefined
             :refer [tokenize-title-using-predefined-style]]
            [com.titlecapitalizer.model.pos-taggers.predefined
             :refer [pos-tag-title-using-predefined-style]]
            [com.titlecapitalizer.model.capitalizers.predefined
             :refer [lower-case-title-using-predefined-style
                     capitalize-pos-tagged-title-using-predefined-style]]
            [com.titlecapitalizer.model.styles.predefined
             :as predefined-styles]))

(def project-version (System/getProperty "com.titlecapitalizer.version"))

(defremote pos-tag-and-capitalize
  [respect-capitalized-input-words style-name title]
  (let [fixed-title
        (if respect-capitalized-input-words
          title
          (lower-case-title-using-predefined-style style-name title))
        pos-tagged-title (pos-tag-title-using-predefined-style style-name
                                                               fixed-title)
        capitalized-title (capitalize-pos-tagged-title-using-predefined-style
                           style-name fixed-title pos-tagged-title)]
    [pos-tagged-title capitalized-title]))

(defremote capitalize
  [style-name title pos-tagged-title]
  (capitalize-pos-tagged-title-using-predefined-style style-name title
                                                      pos-tagged-title))

(defremote load-style
  [style-name]
  (predefined-styles/style style-name))
