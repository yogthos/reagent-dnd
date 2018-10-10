(ns reagent-dnd.util
  (:require [cljs.reader :refer [read-string]]))

(defn deref-or-value
  [val-or-atom]
  (if (satisfies? IDeref val-or-atom) @val-or-atom val-or-atom))

(defn serialize [x]
  #js {"data" (pr-str x)})

(defn unserialize [o]
  (try (read-string (.-data o))
       (catch js/Error e
         false)))

(defn serializable? [x]
  (try (= (unserialize (serialize x))
          x)
       (catch js/Error e
         false)))

(defn safe-call
  "Call a javascript method safely (as a string)"
  [obj method & [args]]
  (when-let [f (aget obj method)]
    (.apply f obj args)))

(defn offset [coords]
  (when coords
    [(.-x coords) (.-y coords)]))

