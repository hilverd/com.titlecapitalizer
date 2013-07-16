(ns com.titlecapitalizer.web-test
  (:require [clojure.test :refer :all]
            [com.titlecapitalizer.model.capitalizers.predefined
             :refer [capitalize-title-using-predefined-style]]))

(deftest first-test
  (is false "Tests should be written"))

(def test-cases
  ["the big spender's budget how-to"
   "author of how-to book on bee-keeping prone to anaphylaxis"
   "governor slams e-book about her re-election campaign"
   "consumers prefer eco-friendly and cheap products"
   "two-thirds vote needed to fund research into blue-green algae biofuel"
   "profits double on word-of-mouth sales"
   "audiences love his man-about-town sophistication"
   "open your own eBay-based boutique"
   "she took the deal off the table"
   "turn off the lights, I'm home"])

(defn run-test-cases
  []
  (doseq [test-case test-cases]
    (println (capitalize-title-using-predefined-style :yahoo test-case))))
