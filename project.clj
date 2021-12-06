(defproject testapp "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.namespace "1.1.0"]
                 [com.datomic/dev-local "1.0.238"]
                 [org.clojure/core.async "1.3.618"]
                 [defcomponent/defcomponent "0.2.2"]
                 [org.clojure/clojurescript "1.10.879"]
                 [compojure "1.6.2"]
                 [ring "1.9.4"]
                 [ring/ring-json "0.5.1"]
                 [re-frame "1.2.0"]
                 [cljs-ajax "0.7.5"]
                 [reagent "1.1.0"]
                 [cljsjs/react "17.0.2-0" :exclusions [cljsjs/react]]
                 [cljsjs/react-dom "17.0.2-0" :exclusions [cljsjs/react]]
                 [org.slf4j/slf4j-api "1.7.26"]
                 [org.slf4j/slf4j-simple "1.7.26"]
                 [prismatic/plumbing "0.5.5"]
                 [ring/ring-defaults "0.3.3"]]
  :plugins [[lein-cljsbuild "1.1.8"]
            [lein-shell "0.5.0"]]
  :source-paths ["src/clj" "src/cljs" "dev"]
  :resource-paths ["resources" "config"]
  :main ^:skip-aot testapp.core
  :target-path "target/%s"

  :profiles {:uberjar {:aot :all
                       :omit-source true
                       :prep-tasks [["shell" "rm" "-rf" "resources/public/js/"]
                                     "compile" ["cljsbuild" "once"]]
                       :cljsbuild {:builds [{:id :main
                                             :source-paths ["src/cljs"]
                                             ;:jar true
                                             :compiler {:main "testapp.core"
                                                        :source-paths ["src/cljs"]
                                                        :optimizations :advanced
                                                        :pretty-print false
                                                        :output-dir "target/js-compiler-output"
                                                        :output-to "resources/public/js/app.js"}}]}}
             :dev {:repl-options {:init-ns user.my}
                   :main user.my
                   :resource-paths ["resources"]
                   :dependencies [[nrepl "0.8.3"]
                                  [org.clojure/tools.namespace "1.1.0"]]}
             :ui {:resource-paths ["resources"]
                  :plugins [[lein-figwheel "0.5.20"]]
                  :dependencies [[com.bhauman/figwheel-main "0.2.15"]
                                 [com.bhauman/figwheel-repl "0.2.15"]
                                 [com.bhauman/rebel-readline-cljs "0.1.4"]
                                 [ring/ring-core "1.9.1"]
                                 [ring/ring-defaults "0.3.2"]
                                 [ring/ring-devel "1.9.1"]
                                 [ring "1.9.1"]
                                 [org.eclipse.jetty.websocket/websocket-servlet "9.4.36.v20210114"]
                                 [org.eclipse.jetty.websocket/websocket-server  "9.4.36.v20210114"] ]
                  :clean-targets ^{:protect false} ["resources/public/js" :target]
                  :cljsbuild {:builds [{:id "testapp"
                                        :source-paths ["src/cljs"]
                                        ;:figwheel {:websocket-url "ws://localhost:3449/figwheel-connect/"}
                                        :compiler {:main "testapp.core"
                                                   :optimizations :none
                                                   :asset-path "js/out"
                                                   :output-to "resources/public/js/app.js"
                                                   :output-dir "resources/public/js/out"
                                                   :preloads [figwheel.core figwheel.main figwheel.repl.preload]}}]}}})

