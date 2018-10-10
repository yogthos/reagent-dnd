(ns reagent-dnd.drag-source
  (:require [reagent.core :as r]
            [reagent-dnd.react-dnd :as react-dnd]
            [reagent-dnd.validate :refer [string-or-hiccup?]]
            [reagent-dnd.util :as util]
            [reagent-dnd.monitor :as monitor]
            [reagent-dnd.connect :as connect]
            ["react-dom" :refer [findDOMNode]])
  (:require-macros [reagent-dnd.validate :refer [validate-args-macro]]))

(def args-desc
  [{:name :child
    :required true
    :type "string | hiccup"
    :validate-fn string-or-hiccup?
    :description "the thing to be dragged"}
   {:name :type
    :required true
    :type "keyword"
    :validate-fn keyword?
    :description "the type of thing to be dragged. e.g. :card or :list"}
   {:name :state
    :required true
    :type "atom"
    :description "an atom (or ratom) to contain the drag-state"}
   {:name :begin-drag
    :required false
    :type "-> serializable"
    :validate-fn fn?
    :description "a function returning the 'dragged item'"}
   {:name :dragging?
    :required false
    :type "-> boolean"
    :validate-fn fn?
    :description "a function identifying whether this represents the currently
dragged component (e.g. if a kanban card moves as you drag it)"}
   {:name :end-drag
    :required false
    :type "(did-drop?, drop-result) -> any"
    :validate-fn fn?
    :description "a function called when dragging stops, guaranteed to be
called once for every begin-drag call. did-drop? is true if drop-target
handled this drop, drop-result is the result of the drop target's :drop, if
any"}
   {:name :can-drag?
    :required false
    :type "-> boolean"
    :validate-fn fn?
    :description "a function returning a boolean indicating whether this
component can be dragged"}
   {:name :drag-preview
    :required false
    :type ""
    :description "a function returning the drag preview, or the drag preview
itself"}])

(defn options [{:keys [begin-drag dragging? end-drag can-drag?]
                :or {begin-drag (constantly {})}}]
  (let [options #js{}]
    (aset options "beginDrag" (fn [props]
                                (let [result (begin-drag)]
                                  (assert (util/serializable? result)
                                          (str "Not Serializable: " (pr-str result)))
                                  (util/serialize result))))
    (when can-drag?
      (aset options "canDrag" (fn [props monitor]
                                (can-drag?
                                 (monitor/monitor->cljsmon monitor
                                                           :except [:can-drag?])))))
    (when end-drag
      (aset options "endDrag" (fn [props monitor component]
                                (end-drag
                                 (monitor/monitor->cljsmon monitor)))))
    (when dragging?
      (aset options "isDragging" (fn [props monitor]
                                   (dragging?
                                    (monitor/monitor->cljsmon monitor :except [:dragging?])))))
    options))

(defn component
  [& {:keys [child type state begin-drag dragging? end-drag can-drag? drag-preview]
      :as args}]
  {:pre [(validate-args-macro args-desc args "drag-source")]}
  (let [options (options (select-keys args [:begin-drag
                                            :dragging?
                                            :end-drag
                                            :can-drag?]))
        wrapper (react-dnd/drag-source
                 (clj->js type)
                 options
                 monitor/props)]
    [(r/adapt-react-class
      (wrapper
       (r/reactify-component
        (r/create-class
         {:component-did-mount
          (fn [this]
            (let [f (-> this
                        (r/props)
                        (monitor/props->cljscon)
                        (aget "connect-drag-preview"))]
              (when drag-preview
                (if (fn? drag-preview)
                  (f [:div (drag-preview)])
                  (f [:div drag-preview])))))
          :component-will-update
          (fn [this [_ next-props]]
            (swap!  state merge (monitor/props->cljsmon next-props)))
          :render
          (fn [this]
            (let [connect-drag-source (-> (r/current-component)
                                          (r/props)
                                          (monitor/props->cljscon)
                                          (aget "connect-drag-source"))]
              (connect-drag-source
               (r/as-element [:div child]))))}))))]))
