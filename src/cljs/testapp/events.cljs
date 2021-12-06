(ns testapp.events
  (:require [ajax.core :refer [GET POST]]
            [re-frame.core :refer [reg-event-db dispatch dispatch-sync reg-fx reg-event-fx]]
            [plumbing.core :refer [map-vals]]))



;; todo fix initial button color
;; todo reset form to initial state after successful post

(def ^:private default-input-state
  {:data-input false
   :data-correct false
   :data nil})

(def ^:private default-form-state
  {:title default-input-state
   :description default-input-state
   :applicant default-input-state
   :assignee default-input-state
   :due-date default-input-state})

(reg-event-db
  :initialize
  (fn [_ _]
    {:applications []
     :has-data false
     :mode :new
     :form default-form-state}))

(reg-event-db
  :form
  (fn [db [_ field value]]
    (case field
      (:title :description) (assoc-in db [:form field]
                              {:data-input true
                               :data-correct (< 10 (count value))
                               :data value})
      (:applicant :assignee) (assoc-in db [:form field]
                               {:data-input true
                                :data-correct (re-matches #"[A-Za-z]{2,} [A-Za-z]{2,}" value)
                                :data value})
      :due-date (assoc-in db [:form field]
                  {:data-input true
                   :data-correct (< (.now js/Date) (new js/Date value))
                   :data value}))))

(reg-event-db
  :applications-list-clicked
  (fn [db _]
    (assoc db :mode :list)))


(reg-event-db
  :new-application-clicked
  (fn [db _]
    (assoc db :mode :new)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(reg-fx
  :alert
  (fn [value]
    (js/alert value)))

(reg-event-fx
  :bad-response
  (fn [_ v]
    (println v)
    {:alert "bad response"}))

(reg-event-db
  :set-applications
  (fn [db [_ data]]
    (assoc db
      :applications data
      :has-data true)))

(defn- process-get-apps-response
  [data]
  (dispatch [:set-applications data]))

(reg-event-fx
  :new-app-success
  (fn [{:keys [db]} _]
    {:db (assoc db :form default-form-state)
     :alert "application created"}))

(reg-fx
  :http
  (fn [[action data]]
    (case action
      :get-apps
      (GET
        "http://localhost:8080/testapp/applications"
        {:handler       process-get-apps-response
         :error-handler #(dispatch [:bad-response %1]) ;; todo
         :response-format :json
         :keywords? true})

      :new-app
      (POST
        "http://localhost:8080/testapp/new-application"
        {:handler #(dispatch [:new-app-success %1])
         :error-handler #(dispatch [:bad-response %1])
         :params data
         :format :json
         :response-format :json
         :keywords? true}))))


(defn- extract-data-from-db
  [db]
  (->> db :form (map-vals (comp str :data))))

(reg-event-fx
  :send-data
  (fn [cofx _]
    {:http [:new-app (extract-data-from-db (:db cofx))]}))

(reg-event-fx
  :refresh-applications
  (fn
    [{:keys [db]} _]
    {:db (assoc db :has-data false)
     :http [:get-apps]}))
