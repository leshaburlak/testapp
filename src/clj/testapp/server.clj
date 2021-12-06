(ns testapp.server
  ;; todo get rid of :use ?
  (:use compojure.core
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [defcomponent :refer [defcomponent]]
            [testapp.datomic-db :refer :all]
            [testapp.datomic-worker :refer [datomic-worker new-application-async]]
            [ring.adapter.jetty :refer :all]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :as response]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params wrap-json-body]]
            [ring.middleware.defaults :refer :all]))


(defroutes app-routes
  (context "/testapp" []
    (GET "/" [] (response/resource-response "index.html" {:root "public"}))
    (-> (GET "/applications" {:keys [datomic-client]}
          (existing-applications datomic-client :stringify true))
      (wrap-json-response))

    (-> (POST "/new-application" {:keys [datomic-worker datomic-client json-params]}
          ;(new-application datomic-client json-params)
          (new-application-async datomic-worker json-params)
          {:body {}})
      (wrap-json-params {:keywords? true})
      (wrap-json-response))

    (route/resources "/")
    (route/not-found "Page not found"))
  (route/not-found "Page not found"))


(defcomponent http-server
  [datomic-client datomic-worker]
  [config]
  (start [{:keys [config datomic-client datomic-worker] :as this}]
    (let [add-db-client-middleware
          (fn [handler]
            (fn [rec]
              (handler (assoc rec
                         :datomic-client datomic-client
                         :datomic-worker datomic-worker))))

          {:keys [base-path port]} (:http config)
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

