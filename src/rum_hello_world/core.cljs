(ns rum-hello-world.core
  (:require [rum.core :as rum]))

(enable-console-print!)

(defonce app-state (atom {:cross-section {:width 175
                                          :depth 100
                                          :thickness 6
                                          :radius 3}}))

(defn render-cross-section
  [{:keys [width depth thickness radius]}]
  [:svg {:id "cross-section-svg"
         :style {:border-style "solid"}
         :width 250 :height 250}
   [:rect {:id "cross-section"
           :stroke "rgb(0,0,0)"
           :fill "white"
           :stroke-width thickness
           :x (- 122 (/ width 2))
           :y (- 122 (/ depth 2))
           :rx radius
           :ry radius
           :width width
           :height depth}]])

(defn render-form-group [type id label value on-change-fn]
  (.log js/console value)
  [:div.form-group
   [:label {:for id} label]
   [:input {:id id :type type :name id :class "form-control"
            :value value
            :on-change on-change-fn}]])

(rum/defc cross-section < rum/reactive [ref]
  (render-cross-section (rum/react ref)))

(rum/defc input-control < rum/reactive [ref id label]
  [:div.row
   (render-form-group "number" id label
                      (or (rum/react ref) 0)
                      #(let [val (js/parseFloat (.. % -target -value))]
                         (when (not (js/isNaN val))
                           (reset! ref val))))])

(rum/defc cross-section-form []
  [:div.row {:style {:padding-right "2%"}}
   [:div.col-md-1 {:style {:padding-right "2%"}}
    (mapv (fn [[id label path]]
            (input-control (rum/cursor-in app-state path) id label))
          [["section-width" "Width"
            [:cross-section :width]]
           ["section-depth" "Depth"
            [:cross-section :depth]]
           ["section-thickness" "thickness"
            [:cross-section :thickness]]
           ["section-radius" "radius"
            [:cross-section :radius]]])]])

(rum/defc page []
  [:div
   (cross-section-form)
   (cross-section (rum/cursor app-state :cross-section))])

(rum/mount (page)
           (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  (swap! app-state update-in [:__figwheel_counter] inc))
