(ns testapp.datomic-db
  (:require [defcomponent :refer :all]
            [datomic.client.api :as d]
            [plumbing.core :refer [?>> map-vals]])
  (:import (java.util UUID Date)
           (java.text SimpleDateFormat)))



(defn existing-applications
  [{:keys [datomic-client]} & {:keys [stringify] :or {stringify false}}]
  (let [db-conn (d/connect datomic-client {:db-name "application"})
        q '[:find ?id ?title ?description ?applicant ?assignee ?due-date
            :where
            [?a :application/id ?id]
            [?a :application/title ?title]
            [?a :application/description ?description]
            [?a :application/applicant ?applicant]
            [?a :application/assignee ?assignee]
            [?a :application/due-date ?due-date]]
        res (d/q q (d/db db-conn))
        decouple (fn [[uuid title desc appl ass compl]]
                   {:id uuid
                    :title title
                    :description desc
                    :applicant appl
                    :assignee ass
                    :due-date compl})]
    (map
      (comp
        (if stringify (partial map-vals str) identity)
        decouple)
      res)))





(def ^:private date-format (new SimpleDateFormat "yyyy-MM-dd"))

(defn new-application
  [{:keys [datomic-client]}
   {:keys [title description applicant assignee due-date]}]
  (let [db-conn (d/connect datomic-client {:db-name "application"})]
    (d/transact db-conn
      {:tx-data [{:application/id (UUID/randomUUID)
                  :application/title title
                  :application/description description
                  :application/applicant applicant
                  :application/assignee assignee
                  :application/due-date (.parse date-format due-date)}]})))



;;;;;;;;;;;

(def ^:private application-schema
  [{:db/ident :application/id
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity
    :db/doc "Id (UUID) of the application"}

   {:db/ident :application/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The title of the application"}

   {:db/ident :application/description
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Description"}

   {:db/ident :application/applicant
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Applicant name"}

   {:db/ident :application/assignee
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Assignee"}

   {:db/ident :application/due-date
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc "Instant when the application is to be completed"}])


(defn- db-exists?
  [datomic-client]
  (contains? (set (d/list-databases datomic-client {})) "application"))

(defn- create-db
  [datomic-client]
  (d/create-database datomic-client {:db-name "application"}))

(defn- schema-exists?
  [datomic-client]
  (not-empty
    (d/q
      '[:find ?ident :where
        [?e :db/ident ?ident]
        [_ :db.install/attribute ?e]

        [(.toString ?ident) ?val]
        [(.startsWith ?val ":application")]
        ]
      (d/db (d/connect datomic-client {:db-name "application"})))))

(defn- create-schema
  [datomic-client]
  (d/transact (d/connect datomic-client {:db-name "application"}) {:tx-data application-schema}))

(defn- maybe-init
  [datomic-client]
  (when-not (db-exists? datomic-client) (create-db datomic-client))
  (when-not (schema-exists? datomic-client) (create-schema datomic-client)))

;;;;;;;


(defcomponent datomic-client
  []
  [config]
  (start [this]
    (let [client-config (get-in config [:db :datomic-client])
          client (d/client client-config)]
      (maybe-init client)
      (prn "datomic client started")
      (assoc this :datomic-client client)))
  (stop [this]
    (dissoc this :datomic-client)))

