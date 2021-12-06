(ns testapp.core
  (:gen-class)
  (:require [testapp.server :as s]
            [clojure.java.io :as io]))


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
  (let [system (defcomponent/system components {:params [(read-string
                                                           (slurp (io/resource "local.clj")))]
                                                :start true})]
    (at-shutdown #(com.stuartsierra.component/stop system))
    (while true
      (Thread/sleep 1000))))

