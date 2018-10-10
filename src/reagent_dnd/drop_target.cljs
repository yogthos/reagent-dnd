(ns reagent-dnd.drop-target
  (:require [reagent.core :as r]
            [reagent-dnd.react-dnd :as react-dnd]
            [reagent-dnd.validate :refer [string-or-hiccup?]]
            [reagent-dnd.util :as util]
            [reagent-dnd.monitor :as monitor]
            [reagent-dnd.connect :as connect])
  (:require-macros [reagent-dnd.validate :refer [validate-args-macro]]))

(def args-desc
  [{:name :child
    :required true
    :type "string | hiccup"
    :validate-fn string-or-hiccup?
    :description "the thing to drop onto"}
   {:name :class-name
    :required false
    :type "string"
    :description "An optional class name to add to the drop target div wrapper"}
   {:name :state
    :required true
    :type "atom | ratom"
    :description "an atom/ratom to hold state, e.g. can-drop, is-over"}
   {:name :types
    :required true
    :type "keyword | [keyword]"
    :description "the type of thing to be dropped. e.g. :card or :list"}
   {:name :drop
    :required false
    :type "-> serializable"
    :validate-fn fn?
    :description "function that returns the 'drop result'"}
   {:name :raw-drop
    :required false
    :type "-> any"
    :validate-fn fn?
    :description "function that returns the 'drop result', is passed raw props & js monitor object & result is not serialized. If specified will be used in preference to :drop "}
   {:name :hover
    :required false
    :type "-> any"
    :validate-fn fn?
    :description "function that is called whenever an item is hovered"}
   {:name :raw-hover
    :required false
    :type "-> any"
    :validate-fn fn?
    :description "function that matches the react-dnd method and is called whenever an item is hovered, is passed (props, monitor, component). If specified will be used in preference to :drop "}
   {:name :begin-drag
    :required false
    :type "-> any"
    :validate-fn fn?
    :description "function that is called whenever a drag is started, return value is available as (:item monitor)"}
   {:name :can-drop?
    :required false
    :type "(dragged-item) -> boolean"
    :validate-fn fn?
    :description "function that indicates whether the item can be accepted by this drop zone"}])

(defn options [{:keys [drop raw-drop can-drop? raw-hover hover]
                :or {drop (constantly nil)}}]
  (let [options #js{}]
    (aset options "drop" (or raw-drop
                             (fn [props monitor]
                               (let [result (drop (monitor/monitor->cljsmon monitor))]
                                 (assert (util/serializable? result)
                                         (str "Not Serializable: " (pr-str result)))
                                 (util/serialize result)))))
    (when can-drop?
      (aset options "canDrop" (fn [props monitor]
                                (can-drop? (monitor/monitor->cljsmon monitor :except [:can-drop?])))))

    (when (or raw-hover hover)
      (aset options "hover" (or raw-hover
                             (fn [props monitor component]
                               (hover (monitor/monitor->cljsmon monitor) component)))))
    options))

(defn make-types [types]
  (assert (or (keyword? types) (vector? types)))
  (if (keyword? types)
    (name types)
    (clj->js (mapv name types))))

(defn component
  [& {:as args
      :keys [child types raw-drop drop raw-hover hover state can-drop? class-name]}]
  {:pre [(validate-args-macro args-desc args "drop-target")]}
  (let [wrapper (react-dnd/drop-target
                 (make-types types)
                 (options args)
                 monitor/props)]
    [(r/adapt-react-class
      (wrapper
       (r/reactify-component
        (r/create-class
         {:component-will-update
          (fn [this [_ next-props] _]
            (reset! state (monitor/props->cljsmon next-props)))
          :render
          (fn [this]
            (let [connect-drop-target (-> (r/current-component)
                                          (r/props)
                                          (monitor/props->cljscon)
                                          (aget "connect-drop-target"))]
              (connect-drop-target
               (r/as-element [:div (merge {} (when class-name
                                               {:class class-name}))
                              child]))))}))))]))
