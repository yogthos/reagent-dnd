(defproject yogthos/react-dnd "0.1.0"
            :description "Reagent wrapper for react-dnd"
            :url "https://github.com/yogthos/reagent-dnd"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies
  [[org.clojure/clojure "1.9.0" :scope "provided"]
   [org.clojure/clojurescript "1.10.339" :scope "provided"]
   [reagent "0.8.1" :scope "provided"]]

  :plugins
  [[lein-cljsbuild "1.1.7"]
   [lein-figwheel "0.5.16"]
   [cider/cider-nrepl "0.15.1"]
   [lein-doo "0.1.10"]]

  :clojurescript? true
  :jar-exclusions [#"\.swp|\.swo|\.DS_Store"]
  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :profiles
  {:dev
   {:dependencies
    [[ring-server "0.5.0"]
     [ring-webjars "0.2.0"]
     [ring "1.7.0"]
     [ring/ring-defaults "0.3.1"]
     [compojure "1.6.1"]
     [hiccup "1.0.5"]
     [nrepl "0.4.4"]
     [binaryage/devtools "0.9.10"]
     [cider/piggieback "0.3.9"]
     [figwheel-sidecar "0.5.16"]]

    :source-paths ["src/clj" "src/cljc" "src/cljs" "env/dev/clj" "env/dev/cljs"]
    :resource-paths ["resources" "env/dev/resources" "target/cljsbuild"]

    :figwheel
    {:server-port      3450
     :nrepl-port       7000
     :nrepl-middleware [cider.piggieback/wrap-cljs-repl
                        cider.nrepl/cider-middleware]
     :css-dirs         ["resources/public/css" "env/dev/resources/public/css"]
     :ring-handler     react-dnd-test.server/app}
    :cljsbuild
    {:builds
     {:app
      {:source-paths ["src" "env/dev/cljs"]
       :figwheel     {:on-jsload "react-dnd-test.test-page/mount-root"}
       :compiler     {:npm-deps {:react "16.5.2"
                                 :react-dom "16.5.2"
                                 :react-dnd "5.0.0"
                                 :react-dnd-html5-backend "5.0.1"}
                      :install-deps true
                      :main          react-dnd-test.dev
                      :asset-path    "/js/out"
                      :output-to     "target/cljsbuild/public/js/app.js"
                      :output-dir    "target/cljsbuild/public/js/out"
                      :source-map-timestamp true
                      :source-map    true
                      :optimizations :none
                      :pretty-print  true}}}}}
   :test
   {:cljsbuild
    {:builds
     {:test
      {:source-paths ["src" "test"]
       :compiler     {:main          react-dnd-test.runner
                      :output-to     "target/test/core.js"
                      :target        :nodejs
                      :optimizations :none
                      :source-map    true
                      :pretty-print  true}}}}
    :doo {:build "test"}}}
    :aliases
    {"test"
     ["do"
      ["clean"]
      ["with-profile" "test" "doo" "node" "once"]]
     "test-watch"
     ["do"
      ["clean"]
      ["with-profile" "test" "doo" "node"]]})
