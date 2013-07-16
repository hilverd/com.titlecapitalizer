(ns com.titlecapitalizer.model.styles.predefined
  (:require [com.titlecapitalizer.model.styles.style-parser :as style-parser]))

(def style-names #{:yahoo :chicago :wikibooks})

(defn style-name?
  [x]
  (contains? style-names x))

(def style-dir "resources/capitalization-styles")

(defn style-file
  [style-name]
  {:pre [(style-name? style-name)]}
  (str style-dir "/" (name style-name) ".clj"))

(defn style
  [style-name]
  {:pre [(style-name? style-name)]}
  (let [style-file (style-file style-name)]
    (style-parser/parse-style-from-file style-file)))

(def predefined-styles (zipmap style-names (map style style-names)))
