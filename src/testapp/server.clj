(ns testapp.server
  ;; todo get rid of :use ?
  (:use compojure.core
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [defcomponent :refer [defcomponent]]
            [testapp.compojure.views :refer :all]
            [testapp.datomic-db :refer :all]
            [ring.adapter.jetty :refer :all]
            [ring.middleware.reload :refer [wrap-reload]]
            [clojure.core.async :as a])
  (:import (org.eclipse.jetty.server Server)))


;; todo get root path from config ?

(def a (atom nil))

(defroutes app-routes
  ;(context "/testapp"
  ;  )
  (GET "/" {:keys [datomic-client]}
    ;(reset! a datomic-client)
    ;(prn datomic-client)
    (ui (existing-applications datomic-client)))
  (route/not-found "Page not found"))

(defcomponent http-server
  [datomic-client]
  [config]
  (start [{:keys [config datomic-client] :as this}]
    (let [add-db-client-middleware
          (fn [handler]
            (fn [rec]
              (handler (assoc rec :datomic-client datomic-client))))

          {:keys [base-path port]} (:http config)
          _ (prn :BASE_PATH base-path)
          app (-> app-routes
                ;;; ?????????
                (wrap-base-url (str "/" base-path))
                (handler/site)
                (wrap-routes add-db-client-middleware)
                (wrap-reload))
          ;reloading-app (wrap-reload #'app)
          jetty-server (run-jetty
                         app
                         {:join? false
                          :port port
                          :host "localhost"})]
      (prn :SERVER_STARTED)
      (assoc this :server jetty-server)))
  (stop [{:keys [server] :as this}]
    (.stop server)
    (dissoc this :server)))


;(def s (let [this (get user.my/system http-server)]
;         (-> this :server )))


(-> a deref keys)
