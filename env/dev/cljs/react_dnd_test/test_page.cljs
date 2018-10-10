(ns react-dnd-test.test-page
  (:require [react-dnd-test.core :as core]
            [reagent.core :as r]))

(defn home-page []
  [core/home])

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
