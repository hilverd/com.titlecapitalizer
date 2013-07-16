(ns com.titlecapitalizer.model.global-setup
  "Setup global variables before other files are loaded, e.g. for turning
   assertions on/off."
  (:require [environ.core :refer [env]]))

;; If we are running in production, disable assertions (including pre- and
;; postconditions) to speed up execution.
(if (env :production) (set! *assert* false))
