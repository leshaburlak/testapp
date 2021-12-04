(ns testapp.events
  (:require [ajax.core :refer [GET POST]]
            [re-frame.core :refer [reg-event-db dispatch dispatch-sync reg-fx reg-event-fx]]
            [plumbing.core :refer [map-vals]]
            [cljs.core.async :as a]))



;; todo fix initial button color
;; todo reset form to initial state after successful post


(reg-event-db
  :initialize
  (fn [_ _]
    {:applications []
     :has-data false
     :mode :new
     :form {:title {:data-input false
                    :data-correct false
                    :data nil}
            :description {:data-input false
                          :data-correct false
                          :data nil}
            :applicant {:data-input false
                        :data-correct false
                        :data nil}
            :assignee {:data-input false
                       :data-correct false
                       :data nil}
            :due-date {:data-input false
                       :data-correct false
                       :data nil}}}))

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





(defn extract-data-from-db
  [db]
  (->> db :form (map-vals (comp str :data))))

(reg-event-db
  :new-app-response
  (fn [db v]
    (println v)
    db))

(reg-event-db
  :bad-response
  (fn [db v]
    (println v)
    db))



(reg-event-db
  :send-data
  (fn [db _]
    (a/go
      (let [d (extract-data-from-db db)]
        (println "SEND DATA RATATATATA")
        (println (str d))
        (POST
          "http://localhost:8080/testapp/new-application"
          {:handler #(dispatch [:new-app-response %1])
           :error-handler #(dispatch [:bad-response %1])
           :params d
           :format :json
           :response-format :json
           :keywords? true})))
    db))


;; todo fx?
(reg-event-db
  :refresh-applications
  (fn
    [db _]
    (GET
      "http://localhost:8080/testapp/applications"
      {:handler       #(dispatch [:process-response %1])
       :error-handler #(dispatch [:bad-response %1]) ;; todo
       :response-format :json
       :keywords? true})

    (assoc db :has-data false)))


(reg-event-db
  :process-response
  (fn
    [db [_ response]]
    (println response)
    (-> db
      (assoc :has-data true)
      (assoc :applications response))))
