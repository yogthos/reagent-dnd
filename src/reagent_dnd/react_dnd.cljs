(ns reagent-dnd.react-dnd
  (:require [reagent.core :as r]
            ["react-dnd" :refer [DragDropContext DropTarget DragSource DragLayer]]
            ["react-dnd-html5-backend" :as html5-backends]))

(def drag-drop-context (memoize DragDropContext))

(def drag-layer DragLayer)

(def drag-source DragSource)

(def drop-target DropTarget)

(def html5-backend (.-default html5-backends))

(def get-empty-image (.getEmptyImage html5-backends))

(defn with-drag-drop-context [backend]
  (fn [component]
    (r/adapt-react-class
     ((drag-drop-context backend)
      (r/reactify-component component)))))
