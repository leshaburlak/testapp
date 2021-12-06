(ns testapp.subs
  (:require [re-frame.core :refer [reg-sub]]))


(reg-sub
  :applications
  (fn [db _]
    (:applications db)))

(reg-sub
  :mode
  (fn [db _]
    (:mode db)))

(reg-sub
  :has-data
  (fn [db _]
    (:has-data db)))

(reg-sub
  :form-data
  (fn [db [_ name]]
    (get-in db [:form name :data])))

(reg-sub
  :form-ok
  (fn [db [_ name]]
    (get-in db [:form name :data-correct])))

(reg-sub
  :form-was-input
  (fn [db [_ name]]
    (get-in db [:form name :data-input])))

(reg-sub
  :data-valid?
  (fn [db _]
    (->> db :form vals (every? :data-correct))))
