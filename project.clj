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
                 [compojure "1.6.2"]
                 [ring "1.9.4"]
                 [re-frame "1.2.0"]
                 [reagent "1.1.0"]
                 ;[org.clojars.frozenlock/reagent-table "0.1.6"]
                 ]
  :main ^:skip-aot testapp.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:main user.my
                   :source-paths ["src" "spec/src" "dev"]
                   :resource-paths ["resources"]
                   :dependencies [[nrepl "0.6.0"]
                                  [org.clojure/tools.namespace "1.1.0"]]}})

