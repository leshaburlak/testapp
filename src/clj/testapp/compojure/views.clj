(ns testapp.compojure.views
  (:use [hiccup core page])
  (:require [reagent.core]
            [re-frame.core :as rf]))





(rf/reg-event-db
  :initialize
  (fn [_ [_ data]]
    {:applications (vec data)
     :mode :list}))


(rf/reg-sub
  :applications
  (fn [db _]
    (:applications db)))

(rf/reg-sub
  :mode
  (fn [db _]
    (:mode db)))


(def KEYS [:id :title :applicant :description :assignee :due-date])

(defn application-list
  []
  [:div.appl_list
   [:h3 "Applications List"]
   [:table
    [:tr (for [k KEYS]
           [:th (name k)])]
    (for [row @(rf/subscribe [:applications])]
      [:tr
       (for [k KEYS]
         [:td (-> row k str)])
       ]
      )]])


(defn ui [data]
  (rf/dispatch-sync [:initialize data])
  (html5
    [:head
     [:title "Applications"]
     [:style (str (slurp "resources/css/styles.css"))]]
    [:body
     (if (= :list @(rf/subscribe [:mode]))
       [application-list]
       [:div.appl_create
        [:h3 "Create an application:"]
        [:form
         [:label]]])]))



;(defn render
;  []
;  (reagent.dom/render [ui]
;    (js/document.getElementById "app")))

;(defn run
;  []
;  (rf/dispatch-sync [:initialize]) ;; put a value into application state
;  (render))
