(ns testapp.core
  ;(:use [hiccup core page])
  (:require [reagent.dom]
            [reagent.ratom]
            [re-frame.core :as rf]
            [testapp.events]))


(rf/reg-event-db
  :initialize
  (fn [_ [_ data]]
    {:applications (vec data)
     :has-data false
     :mode :list}))


(rf/reg-sub
  :applications
  (fn [db _]
    (:applications db)))

(rf/reg-sub
  :mode
  (fn [db _]
    (:mode db)))

(rf/reg-sub
  :has-data
  (fn [db _]
    (:has-data db)))


(def KEYS [:id :title :applicant :description :assignee :due-date])

(defn application-list
  []
  [:div.appl_list
   [:h3 "Applications List"]
   (if @(rf/subscribe [:has-data])
     [:table
      [:thead [:tr (for [k KEYS]
                     [:th (name k)])]]
      [:tbody
       (for [row @(rf/subscribe [:applications])]
         [:tr
          (for [k KEYS]
            [:td (k row)])])]]
     [:span "loading..."])])


;(defn ui []
;  [:head
;   [:title "Applications"]
;   [:style (str (slurp "resources/css/styles.css"))]]
;  [:body
;   (if (= :list @(rf/subscribe [:mode]))
;     [application-list]
;     [:div.appl_create
;      [:h3 "Create an application:"]
;      [:form
;       [:label]]])])



;(defn selector
;  []
;  [:div {:class "grid-container"}
;   [:div {:class "grid-child selected"}
;    "applications list"]
;   [:div {:class "grid-child"}
;    "new application"]])


(defn ui []
  (if (= :list @(rf/subscribe [:mode]))
    [application-list]
    [:div.appl_create
     [:h3 "Create an application:"]
     [:form
      [:label]]]))







(.log js/console "PRIVET EBAT")

(rf/dispatch-sync [:initialize [#_{:id "uuid"
                                 :title "title"
                                 :description "desc"
                                 :applicant "appl"
                                 :assignee "ass"
                                 :due-date "compl"} ]])



(rf/dispatch [:refresh-applications])

(reagent.dom/render [ui]
  (js/document.getElementById "app"))


#_(defn run
  []
  )
