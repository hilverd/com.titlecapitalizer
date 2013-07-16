(ns com.titlecapitalizer.model.string-utils
  (:require [clojure.string :as string]))

(defn enclose-in-quotes [string] (str "\"" string "\""))

(defn enclose-in-square-brackets [string] (str "[" string "]"))

(defn enclose-in-parentheses [string] (str "(" string ")"))

(defn capitalize-first-letter
  "Capitalize the first letter occurring in the string x, if any."
  [x]
  {:pre [(string? x)]
   :post [(string? %)]}
  (string/replace-first x #"[a-zA-Z]" #(string/capitalize %)))

(defn lower-case-first-letter
  "Convert the first letter occurring in the string x, if any, to lower case."
  [x]
  {:pre [(string? x)]
   :post [(string? %)]}
  (string/replace-first x #"[a-zA-Z]" #(string/lower-case %)))
