(ns com.titlecapitalizer.main.view
  (:require [domina :as dom]
            [com.titlecapitalizer.main.render :as render]))

;; DOM atoms
(def input-title-field (atom nil))
(def respect-capitalized-input-words (atom nil))
(def toggle-view-details-link (atom nil))
(def details-icon (atom nil))
(def loading-details-icon (atom nil))
(def style-rules-div (atom nil))
(def analyze-button (atom nil))
(def analyze-button-icon (atom nil))
(def pos-tagged-title-div (atom nil))
(def style-radios (atom nil))
(def capitalized-title-field (atom nil))

(defn initialize-dom-atoms
  []
  (reset! input-title-field (dom/by-id "inputTitle"))
  (reset! respect-capitalized-input-words
          (dom/by-id "respectCapitalizedInputWords"))
  (reset! toggle-view-details-link (dom/by-id "toggleViewDetails"))
  (reset! details-icon (dom/by-id "toggleViewDetailsIcon"))
  (reset! loading-details-icon (dom/by-id "loadingDetailsIcon"))
  (reset! style-rules-div (dom/by-id "capitalizationStyleRules"))
  (reset! analyze-button (dom/by-id "analyzeTitle"))
  (reset! analyze-button-icon (dom/by-id "analyzeTitleIcon"))
  (reset! pos-tagged-title-div (dom/by-id "posTaggedTitle"))
  (reset! style-radios (dom/nodes (dom/by-class "style-radio")))
  (reset! capitalized-title-field (dom/by-id "capitalizedTitle")))

;; DOM atoms for capitalization style details
(def hy-comp-style-radios (atom nil))

(defn initialize-details-dom-atoms
  []
  (reset! hy-comp-style-radios (dom/nodes
                                (dom/by-class "hy-comp-style-radio"))))

(defn input-title-field-value
  []
  (dom/value @input-title-field))

(defn respect-capitalized-input-words?
  []
  (dom/value (dom/by-id "respectCapitalizedInputWords")))

(defn show-details-expanded
  []
  (dom/set-attr! @details-icon "class" "icon-collapse"))

(defn show-details-collapsed
  []
  (dom/set-attr! @details-icon "class" "icon-expand"))

(defn show-loading-details-in-progress
  []
  (dom/set-attr! @loading-details-icon "class" "icon-spinner icon-spin"))

(defn show-loading-details-not-in-progress
  []
  (dom/set-attr! @loading-details-icon "class" ""))

(defn disable-analyze-button
  []
  (dom/add-class! @analyze-button "disabled"))

(defn enable-analyze-button
  []
  (dom/remove-class! @analyze-button "disabled"))

(defn show-analyzing-in-progress
  []
  (dom/set-attr! @analyze-button-icon
                 "class" "icon-spinner icon-spin icon-white"))

(defn show-analyzing-not-in-progress
  []
  (dom/set-attr! @analyze-button-icon "class" "icon-ok icon-white"))

(defn pos-tagged-token-span
  [token-index]
  (dom/by-id (str "pos-tagged-token-" token-index)))

(defn checked-style-name
  []
  (keyword (some dom/value @style-radios)))

(defn set-style-details-div
  [style hy-comp-style]
  (let [style-details-div (dom/by-id "capitalizationStyleDetails")]
    (dom/set-html! style-details-div render/style-loading-content-html)
    (let [html (render/style-content-html style hy-comp-style)]
      (dom/swap-content! style-details-div html)
      (js/enableClickovers)
      (js/enableRuleTypeaheads))))

(defn checked-hy-comp-style
  []
  (keyword (some dom/value (dom/nodes (dom/by-class "hy-comp-style-radio")))))

(defn set-pos-tagged-title-div
  [pos-tagged-title]
  (let [html (render/pos-tagged-title-html pos-tagged-title)]
    (dom/set-html! @pos-tagged-title-div html)
    (js/enableClickovers)))

(defn set-pos-tagged-token-span
  [mark-as-changed token-index pos-tagged-token]
  (let [html (render/pos-tagged-token-html mark-as-changed token-index
                                           pos-tagged-token)]
    (dom/set-html! (pos-tagged-token-span token-index) html)
    (js/enableClickovers)))

(defn set-capitalized-title
  [title]
  (dom/set-value! @capitalized-title-field title))
