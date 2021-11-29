(ns testapp.datomic-db
  (:require [defcomponent :refer :all]
            [datomic.client.api :as d]
            [clojure.core.async :as a]))



(defn existing-applications
  [{:keys [datomic-client]}]
  (let [db-conn (d/connect datomic-client {:db-name "application"})
        q '[:find ?id ?title ?description ?applicant ?assignee ?due-date
            :where
            [_ :application/id ?id]
            [_ :application/title ?title]
            [_ :application/description ?description]
            [_ :application/applicant ?applicant]
            [_ :application/assignee ?assignee]
            [_ :application/due-date ?due-date]]
        res (d/q q (d/db db-conn))
        decouple (fn [[uuid title desc appl ass compl]]
                   {:id uuid
                    :title title
                    :description desc
                    :applicant appl
                    :assignee ass
                    :due-date compl})]
    (map decouple res)))



(defcomponent datomic-client
  []
  [config]
  (start [this]
    (let [client-config (get-in config [:db :datomic-client])]
      (prn "datomic client started")
      (assoc this :datomic-client (d/client client-config))))
  (stop [this]
    (dissoc this :datomic-client)))







;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;
;(let [dc (get testapp.core/s datomic-client)]
;  (keys dc)
;  (d/list-databases dc {}))
;
;
;(def application-schema
;  [
;   ;; todo [[:db/unique :db.unique/identity]]
;
;   {:db/ident :application/id
;    :db/valueType :db.type/uuid
;    :db/cardinality :db.cardinality/one
;    :db/doc "Id (UUID) of the application"}
;
;
;
;   {:db/ident :application/title
;    :db/valueType :db.type/string
;    :db/cardinality :db.cardinality/one
;    :db/doc "The title of the application"}
;
;   {:db/ident :application/description
;    :db/valueType :db.type/string
;    :db/cardinality :db.cardinality/one
;    :db/doc "Description"}
;
;   {:db/ident :application/applicant
;    :db/valueType :db.type/string
;    :db/cardinality :db.cardinality/one
;    :db/doc "Applicant name"}
;
;   {:db/ident :application/assignee
;    :db/valueType :db.type/string
;    :db/cardinality :db.cardinality/one
;    :db/doc "Assignee"}
;
;   {:db/ident :application/due-date
;    :db/valueType :db.type/instant
;    :db/cardinality :db.cardinality/one
;    :db/doc "Instant when the application is to be completed"}])
;
;(def test-data
;  [{:application/id (java.util.UUID/randomUUID)
;    :application/title "test titel"
;    :application/description "test description"
;    :application/applicant "test applicant"
;    :application/assignee "test assignee"
;    :application/due-date (new java.util.Date)}])


;(let [client (:datomic-client (get user.my/system datomic-client))]
;  ;(d/list-databases client {})
;  (d/create-database client {:db-name "application"})
;  (let [db-conn (d/connect client {:db-name "application"})]
;    (d/transact db-conn {:tx-data application-schema})
;    (d/transact db-conn {:tx-data test-data})
;    (d/q
;      '[:find ?ident :where
;        [?e :db/ident ?ident]
;        [_ :db.install/attribute ?e]
;
;        [(.toString ?ident) ?val]
;        [(.startsWith ?val ":applicatio")]
;        ]
;      (d/db db-conn))))
;
;

;(let [client (:datomic-client (get user.my/system datomic-client))
;      db-conn (d/connect client {:db-name "application"})
;      res (d/q
;            '[:find ?id ?title ?description ?applicant ?assignee ?due-date
;              :where
;              [_ :application/id ?id]
;              [_ :application/title ?title]
;              [_ :application/description ?description]
;              [_ :application/applicant ?applicant]
;              [_ :application/assignee ?assignee]
;              [_ :application/due-date ?due-date]]
;            (d/db db-conn))
;
;      [[uuid title desc appl ass compl]] res]
;
;  {:uuid uuid
;   :title title
;   :decription desc
;   :application appl
;   :assignee ass
;   :due-date compl}
;  )

;
;(let [this (get user.my/system datomic-client)]
;  (existing-applications this))