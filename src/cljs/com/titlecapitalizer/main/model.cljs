(ns com.titlecapitalizer.main.model)

;; The input title that was used the last time the "Analyze" button was clicked
;; (or the enter key was pressed).
(def last-used-input-title (atom nil))

;; The "leave already capitalized input words unchanged" that was used the last
;; time the "Analyze" button was clicked.
(def last-used-respect-capitalized-input-words (atom nil))

;; The name of the style that was used the last time the "Analyze" button was
;; clicked.
(def last-used-style-name (atom nil))

;; The POS-tagged title that was calculated the last time the "Analyze" was
;; clicked.
(def last-calculated-pos-tagged-title (atom nil))

;; The POS-tagged title that is currently being displayed.
(def pos-tagged-title (atom nil))

;; The name of the style that is currently being displayed in the "details"
;; section.
(def details-style-name (atom nil))

;; The style that is currently being displayed in the "details" section.
(def style (atom nil))

;; True if the "details" section is currently expanded.
(def details-expanded (atom false))

;; True if the currently selected style was modified since the last time the
;; "Analyze" button was clicked.
(def style-modified (atom false))
