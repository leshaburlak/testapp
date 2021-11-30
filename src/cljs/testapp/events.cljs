(ns testapp.events
  (:require [ajax.core :refer [GET]]
            [re-frame.core :refer [reg-event-db dispatch]]))

(reg-event-db        ;; <-- register an event handler
  :refresh-applications        ;; <-- the event id
  (fn                ;; <-- the handler function
    [db _]

    ;; kick off the GET, making sure to supply a callback for success and failure
    (GET
      "http://localhost:8080/testapp/applications"
      {:handler       #(dispatch [:process-response %1])
       :error-handler #(dispatch [:bad-response %1])
       :response-format :json
       :keywords? true
       })

    ;; update a flag in `app-db` ... presumably to cause a "Loading..." UI
    (assoc db :loading? true)))    ;; <3> return an updated db


(reg-event-db
  :process-response
  (fn
    [db [_ response]]
    (println response)
    (-> db
      (assoc :has-data true)
      (assoc :applications response))))
