(ns testapp.core
  ;
  (:require [reagent.dom]
            [reagent.ratom]
            [re-frame.core :as rf]
            [testapp.events]
            [testapp.subs]))

(def KEYS [:id :title :applicant :description :assignee :due-date])

(defn application-list
  []
  [:div.appl_list
   (if @(rf/subscribe [:has-data])
     [:table
      [:thead [:tr (for [k KEYS]
                     [:th {:key k} (name k)])]]
      [:tbody
       (for [row @(rf/subscribe [:applications])]
         [:tr {:key (:id row)}
          (for [k KEYS]
            [:td {:key (str (:id row) k)} (k row)])])]]
     [:span "loading..."])])


(defn selector
  []
  [:div {:class "grid-container"}
   [:div {:class ["grid-child" (when (= :list @(rf/subscribe [:mode])) "selected")]
          :on-click #(do
                       (rf/dispatch [:refresh-applications])
                       (rf/dispatch-sync [:applications-list-clicked]))}
    "applications list"]
   [:div {:class ["grid-child" (when (= :new @(rf/subscribe [:mode])) "selected")]
          :on-click #(rf/dispatch-sync [:new-application-clicked])}
    "new application"]])


(defn input-component
  [id type title placeholder]
  [:div
   [:label {:for (name id)} title]
   [:input {:type type
            :id (name id)
            :class [(when @(rf/subscribe [:form-was-input id])
                      (if @(rf/subscribe [:form-ok id])
                        "correct"
                        "wrong"))]
            :placeholder placeholder
            :value @(rf/subscribe [:form-data id])
            :on-change #(rf/dispatch [:form id (-> % .-target .-value)])}]])

(defn new-application
  []
  [:div
   [:h3 "Create an application:"]
   [:div {:class "form"}
    [input-component :title "text" "title" "Title"]
    [input-component :description "textarea" "description" "blah-blah"]
    [input-component :applicant "text" "applicant" "John Doe"]
    [input-component :assignee "text" "assignee" "Jane Doe"]
    [input-component :due-date "date" "due date" nil]
    [:button {:disabled (not @(rf/subscribe [:data-valid?]))
              :on-click #(rf/dispatch [:send-data])}
     "Submit"]]])


(defn ui []
  [:div
   [selector]
   (if (= :list @(rf/subscribe [:mode]))
     [application-list]
     [new-application])])



(rf/dispatch-sync [:initialize])
(rf/dispatch [:refresh-applications])

(reagent.dom/render [ui]
  (js/document.getElementById "app"))

;; todo change to ^:export main ?
