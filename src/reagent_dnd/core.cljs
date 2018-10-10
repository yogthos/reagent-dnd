(ns reagent-dnd.core
  (:require [reagent.core :as r]
            [reagent-dnd.react-dnd :as react-dnd]
            [reagent-dnd.util :as util]
            [reagent-dnd.drag-source :as drag-source]
            [reagent-dnd.validate :refer [string-or-hiccup?]]
            [reagent-dnd.drag-layer :as drag-layer]
            [reagent-dnd.drop-target :as drop-target]
            [reagent-dnd.monitor :as monitor]
            [cljs.reader :refer [read-string]])
  (:require-macros [reagent-dnd.validate :refer [validate-args-macro]]))

(def drag-source drag-source/component)
(def drop-target drop-target/component)
(def drag-layer drag-layer/component)
(def drag-drop-context react-dnd/drag-drop-context)
(def html5-backend react-dnd/html5-backend)
(def get-empty-image react-dnd/get-empty-image)

(defn with-drag-drop-context [backend component]
  (r/adapt-react-class
   ((drag-drop-context backend)
    (r/reactify-component component))))

