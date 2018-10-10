(ns reagent-dnd.drag-layer
  (:require [reagent.core :as r]
            [reagent-dnd.monitor :as monitor]
            [reagent-dnd.react-dnd :as react-dnd]
            [reagent-dnd.util :as util]
            [reagent-dnd.connect :as connect]
            [reagent-dnd.monitor :as monitor]))

(defn component
  [& {:keys [child state]}]
  (let [wrapper (react-dnd/drag-layer monitor/drag-layer-props)]
    [(r/adapt-react-class
      (wrapper
       (r/reactify-component
        (r/create-class
         {:component-will-update
          (fn [this [_ next-props] _]
            (reset! state (monitor/props->cljsmon next-props)))
          :render
          (fn [this]
            (r/as-element child))}))))]))
