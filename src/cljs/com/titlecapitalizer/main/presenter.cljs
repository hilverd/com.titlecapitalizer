(ns com.titlecapitalizer.main.presenter
  (:require [cljs.reader :refer [read-string]]
            [domina.events :as ev]
            [shoreleave.remotes.http-rpc :refer [remote-callback]]
            [com.titlecapitalizer.model.styles.style :as style]
            [com.titlecapitalizer.main.model :as model]
            [com.titlecapitalizer.main.view :as view]))

(def ENTER-KEY 13)

(defn new-input-available?
  []
  (let [title (view/input-title-field-value)
        respect-capitalized-input-words (view/respect-capitalized-input-words?)
        style-name (view/checked-style-name)]
    (or
     @model/style-modified
     (not= style-name @model/last-used-style-name)
     (not= respect-capitalized-input-words
           @model/last-used-respect-capitalized-input-words)
     (and (not (clojure.string/blank? title))
          (not= title @model/last-used-input-title))
     (not= @model/pos-tagged-title @model/last-calculated-pos-tagged-title))))

(defn enable-or-disable-analyze-button
  "Depending on the current input title and the one that was last analyzed,
   either disable or enable the submit button."
  []
  (let [title (view/input-title-field-value)]
    (if (new-input-available?)
      (view/enable-analyze-button)
      (view/disable-analyze-button))))

(defn pos-tag-and-capitalize-title
  [e]
  (ev/prevent-default e)
  (if (new-input-available?)
    (let [title (view/input-title-field-value)
          respect-capitalized-input-words
          (view/respect-capitalized-input-words?)
          style-name (view/checked-style-name)]
      (reset! model/last-used-input-title title)
      (reset! model/last-used-respect-capitalized-input-words
              respect-capitalized-input-words)
      (reset! model/last-used-style-name style-name)
      (reset! model/style-modified false)
      (view/show-analyzing-in-progress)
      (remote-callback :pos-tag-and-capitalize
                       [respect-capitalized-input-words style-name title]
                       (fn [[pos-tagged-title capitalized-title]]
                         (reset! model/last-calculated-pos-tagged-title
                                 pos-tagged-title)
                         (reset! model/pos-tagged-title pos-tagged-title)
                         (view/set-pos-tagged-title-div pos-tagged-title)
                         (view/set-capitalized-title
                          (.toString capitalized-title))
                         (view/show-analyzing-not-in-progress)))
      (view/disable-analyze-button))))

;; This is being called by the clickover updater when the user selects a POS tag
(defn ^:export updatePosTag
  [token-index new-pos-tag]
  (let [token-index (read-string token-index)
        style-name (view/checked-style-name)
        old-pos-tagged-token (nth @model/pos-tagged-title token-index)
        [old-token old-pos-tag old-index] old-pos-tagged-token
        new-pos-tagged-token [old-token new-pos-tag old-index]
        new-pos-tagged-title (assoc @model/pos-tagged-title token-index
                                    new-pos-tagged-token)
        new-pos-tag-different-from-last-calculated
        (let [[_ calc-pos-tag _] (nth @model/last-calculated-pos-tagged-title
                                      token-index)]
          (not= calc-pos-tag new-pos-tag))]
    (remote-callback :capitalize
                     [style-name @model/last-used-input-title
                      new-pos-tagged-title]
                     (fn [new-capitalized-title]
                       (reset! model/pos-tagged-title new-pos-tagged-title)
                       (view/set-pos-tagged-token-span
                        new-pos-tag-different-from-last-calculated
                        token-index new-pos-tagged-token)
                       (view/set-capitalized-title
                        (.toString new-capitalized-title))
                       (enable-or-disable-analyze-button)))))

;; TODO: implement this.
(defn modify-style
  []
  (reset! model/style-modified true))

(defn select-hy-comp-style
  []
  (modify-style))

(defn setup-details-listeners
  []
  (view/initialize-details-dom-atoms)
  (ev/listen! @view/hy-comp-style-radios :click
              select-hy-comp-style))

(defn load-details
  []
  (let [style-name (view/checked-style-name)]
    (view/show-loading-details-in-progress)
    (remote-callback :load-style
                     [style-name]
                     (fn [style]
                       (let [hy-comp-style
                             (style/hy-comp-style-for-style style)]
                         (view/set-style-details-div style hy-comp-style)
                         (reset! model/details-style-name style-name)
                         (reset! model/style style)
                         (reset! model/style-modified false)
                         (setup-details-listeners)
                         (view/show-loading-details-not-in-progress))))))

(defn select-style
  []
  (when (and @model/details-expanded
             (not= (view/checked-style-name) @model/details-style-name))
    (load-details))
  (reset! model/style-modified false)
  (enable-or-disable-analyze-button))

(defn ^:export collapseDetails
  []
  (reset! model/details-expanded false)
  (view/show-details-collapsed))

(defn ^:export expandDetails
  []
  (reset! model/details-expanded true)
  (view/show-details-expanded)
  (when (not= @model/details-style-name (view/checked-style-name))
    (load-details)))

(defn ^:export init
  []
  (when (and js/document (aget js/document "getElementById"))
    (view/initialize-dom-atoms)
    (ev/listen! @view/input-title-field :keyup
                enable-or-disable-analyze-button)
    (ev/listen! @view/input-title-field :change
                enable-or-disable-analyze-button)
    (ev/listen! @view/input-title-field :keypress
                #(if (= ENTER-KEY (:keyCode %))
                   (pos-tag-and-capitalize-title %)))
    (ev/listen! @view/respect-capitalized-input-words :click
                enable-or-disable-analyze-button)
    (ev/listen! @view/style-radios :click
                select-style)
    (ev/listen! @view/toggle-view-details-link :click
                ev/prevent-default)
    (ev/listen! @view/analyze-button :click
                pos-tag-and-capitalize-title)))
