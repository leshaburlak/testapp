(ns testapp.core
  (:gen-class)
  (:require [testapp.server :as s]))


(def components [s/http-server])

(defn at-shutdown
  [f]
  (-> (Runtime/getRuntime)
    (.addShutdownHook (Thread. (bound-fn []
                                 (prn "Shutdown!")
                                 (f))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [system (defcomponent/system components {:file-config "config/local.clj"
                                                :start true})]
    (at-shutdown #(com.stuartsierra.component/stop system))
    (while true
      (Thread/sleep 1000))))

