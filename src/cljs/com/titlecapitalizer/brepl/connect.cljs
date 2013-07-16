(ns com.titlecapitalizer.brepl.connect
  (:require [clojure.browser.repl :as repl]))

;; See https://github.com/emezeske/lein-cljsbuild/blob/0.3.2/doc/REPL.md#repl-listen

(repl/connect "http://localhost:9000/repl")
