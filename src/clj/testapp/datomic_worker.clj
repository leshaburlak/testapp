(ns testapp.datomic-worker
  (:require [testapp.datomic-db :refer [datomic-client new-application]]
            [clojure.core.async :as a]
            [defcomponent :refer [defcomponent]]))



(defn- put-to-db
  [[this data]]
  (new-application (:datomic-client this) data))

(defn- run-threads
  [{:keys [chan-buffer-n threads-n real-threads]}]
  (let [c (a/chan chan-buffer-n)

        thr-chans
        (if real-threads
          (->> (for [_ (range threads-n)]
                 (a/thread
                   (when-let [v (a/<!! c)]
                     (try
                       (put-to-db v)
                       (catch Exception e
                         (println e))))))
            (doall))
          [(a/go
             (when-let [v (a/<! c)]
               (try
                 ;(prn (put-to-db v))
                 (put-to-db v)
                 ;(prn :INSERT_OK)
                 (catch Exception e
                   (println e)))))])]
    [c thr-chans]))



(defn new-application-async
  [{:keys [input-chan] :as this} v]
  (a/put! input-chan [this v]))


(defcomponent datomic-worker
  [datomic-client]
  [config]
  (start
    [this]
    (let [[c thr-chans] (run-threads (:worker config))]
      (println :WORKER_STARTED)
      (assoc this
        :input-chan c
        :worker-chans thr-chans)))
  (stop
    [{:keys [input-chan worker-chans] :as this}]
    (a/close! input-chan)
    (loop [[c & cx] worker-chans]
      (when c
        (a/<!! c)
        (recur cx)))
    (println :WORKER_STOPPED)))

