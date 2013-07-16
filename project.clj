(defproject com.titlecapitalizer "1.0.0-SNAPSHOT"
  :description "Capitalize titles according to various styles."
  :url "http://www.titlecapitalizer.com/"
  :source-paths ["src/clj"]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [ring/ring-jetty-adapter "1.1.8"]
                 [ring/ring-devel "1.1.8"]
                 [ring-basic-authentication "1.0.1"]
                 [environ "0.4.0"]
                 [com.cemerick/drawbridge "0.0.6"]
                 [com.cemerick/piggieback "0.0.4"]
                 [hiccup "1.0.3"]
                 [domina "1.0.2-SNAPSHOT"]
                 [crate "0.2.4"]
                 [shoreleave/shoreleave-remote-ring "0.3.0"]
                 [shoreleave/shoreleave-remote "0.3.0"]
                 [clojure-opennlp "0.2.2"]]
  :min-lein-version "2.1.3"
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :plugins [[environ/environ.lein "0.3.1"]
            [lein-cljsbuild "0.3.0"]]
  :hooks [environ.leiningen.hooks]
  :profiles {:production {:env {:production true}}}
  :cljsbuild
  {:builds
   [{:id "dev",
     :source-paths
     ["src/cljs/com/titlecapitalizer/brepl"
      "src/cljs/com/titlecapitalizer/main"],
     :compiler
     {:pretty-print true,
      :output-to "resources/public/js/dev/main.js",
      :optimizations :whitespace}}
    {:id "prd",
     :source-paths ["src/cljs/com/titlecapitalizer/main"],
     :compiler
     {:pretty-print false,
      :output-to "resources/public/js/main.js",
      :optimizations :advanced}}],
   :crossovers
   [com.titlecapitalizer.model.string-utils
    com.titlecapitalizer.model.styles.style]
   :crossover-path "src/cljs/com/titlecapitalizer/crossover"})
