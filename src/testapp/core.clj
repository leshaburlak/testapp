(ns testapp.core
  (:require [testapp.datomic-db :as d]
            [testapp.server :as s]))





(def components [#_d/datomic-client #_d/timer-component s/http-server])

(defn at-shutdown
  [f]
  (-> (Runtime/getRuntime)
    (.addShutdownHook (Thread. (bound-fn []
                                 (prn "Shutdown!")
                                 (f))))))

(def s nil)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [system (defcomponent/system components {:file-config "config/local.clj"
                                                :start true})]
    (alter-var-root #'s (constantly system))
    (at-shutdown #(com.stuartsierra.component/stop system))
    (while true
      (Thread/sleep 1000))))


;
;(-> s :datomic-client keys)
;;(-> s  keys)
;;
;(future (-main))
;;;
;(let [dc (get s d/datomic-client)]
;  dc)


