(ns user.my
    (:require
      [clojure.tools.namespace.repl :refer [refresh set-refresh-dirs]]
      [com.stuartsierra.component :as component]
      [defcomponent]
      [testapp.server :refer [http-server]]))

(set-refresh-dirs "src/" "dev/")

(def system nil)

(defn init
      []
      (alter-var-root
        #'system
        (constantly
          (defcomponent/system
            [http-server]
            {:file-config "config/local.clj"}))))

(defn start
      []
      (alter-var-root #'system component/start))

(defn stop
      []
      (alter-var-root
        #'system
        (fn [s] (when s (component/stop s) nil))))

(defn go
      []
      (init)
      (start))

(defn reset
      []
      (stop)
      (refresh :after 'user.my/go)
      :ok)

(defn reload
      []
      (stop)
      (refresh)
      :ok)
