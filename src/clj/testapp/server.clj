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
            [clojure.core.async :as a]
            [ring.util.response :as response]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params wrap-json-body]]
            [ring.middleware.defaults :refer :all]
            )
  (:import (org.eclipse.jetty.server Server)))


;; todo get root path from config ?

(def a (atom nil))

(defroutes app-routes
  (context "/testapp" []

    (GET "/" [] (response/resource-response "index.html" {:root "public"}))

    (-> (GET "/applications" {:keys [datomic-client]}
          (existing-applications datomic-client :stringify true))
      (wrap-json-response))


    (-> (POST "/new-application" {:keys [datomic-client json-params body] :as all}
          (prn (:params all))
          (reset! a all)

          (new-application datomic-client json-params)
          {:body {:id "hui"}})
      (wrap-json-params {:keywords? true})
      (wrap-json-response)
      )

    (route/resources "/")
    (route/not-found "Page not found"))
  (route/not-found "Page not found")

  )


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
                (wrap-routes add-db-client-middleware)
                (wrap-reload))
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

;
;(def s (let [this (get user.my/system http-server)]
;         (-> this :server )))
;
;
;(-> a deref keys)
