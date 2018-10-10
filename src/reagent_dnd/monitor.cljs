(ns reagent-dnd.monitor
  (:require [reagent-dnd.util :as util]))

(def monitor-fields
  {:can-drag?                      {:method "canDrag"}
   :can-drop?                      {:method "canDrop"}
   :over?                          {:method "isOver"}
   :shallow-over?                  {:method "isOver"
                                    :args #js[#js{"shallow" true}]}
   :dragging?                      {:method "isDragging"}
   :item-type                      {:method "getItemType"
                                    :post-fn keyword}
   :item                           {:method "getItem"
                                    :post-fn util/unserialize}
   :drop-result                    {:method "getDropResult"
                                    :post-fn util/unserialize}
   :dropped?                       {:method "didDrop"}
   :initial-client-offset          {:method "getInitialClientOffset"
                                    :post-fn util/offset}
   :initial-source-client-offset   {:method "getInitialSourceClientOffset"
                                    :post-fn util/offset}
   :client-offset                  {:method "getClientOffset"
                                    :post-fn util/offset}
   :difference-from-initial-offset {:method "getDifferenceFromInitialOffset"
                                    :post-fn util/offset}
   :source-client-offset           {:method "getSourceClientOffset"
                                    :post-fn util/offset}})

(defn monitor->cljsmon [monitor & {:keys [except]}]
  (reduce (fn [accu [key {:keys [method post-fn args]
                         :or {post-fn identity}}]]
            (if-let [res (post-fn (util/safe-call monitor method args))]
              (assoc accu key res)
              accu))
          {}
          (apply dissoc monitor-fields except)))

(defn monitor->sermon [monitor]
  (-> monitor
      (monitor->cljsmon)
      (util/serialize)))

(defn connect->cljscon [connect]
  {:connect-drop-target (util/safe-call connect "dropTarget")
   :connect-drag-source (util/safe-call connect "dragSource")
   :connect-drag-preview (util/safe-call connect "dragPreview")})

(defn props [connect monitor]
  (clj->js {:monitor (monitor->sermon monitor)
            :connect (connect->cljscon connect)}))

(defn drag-layer-props [monitor]
  (clj->js {:monitor (monitor->sermon monitor)}))

(defn props->cljsmon [props]
  (util/unserialize (:monitor props)))

(defn props->cljscon [props]
  (:connect props))

