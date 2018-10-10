(ns react-dnd-test.core
  (:require
    [reagent.core :as r]
    [reagent-dnd.core :as dnd])
  (:require-macros
    [reagent.ratom :refer [reaction]]))

(defonce state (r/atom {:knight-position [0 0]}))

(defn knight-at? [position]
  (reaction (= (:knight-position @state) position)))

(defn can-move-knight? [position]
  (reaction
    (let [[kx ky] (:knight-position @state)
          [tx ty] position
          dx (Math/abs (- kx tx))
          dy (Math/abs (- ky ty))]
      (or
        (and (= dx 2) (= dy 1))
        (and (= dy 2) (= dx 1))))))

(defn knight-span [drag-state]
  [:span {:style
          {:cursor :move
           :opacity (if (:dragging? @drag-state) 0.5 1)}}
   "♘"])

(defn knight []
  (let [drag-state (r/atom {})]
    [dnd/drag-source
     :type :knight
     :state drag-state
     :child [knight-span drag-state]]))

(defn square [& {:keys [black? piece drag-state]}]
  [:div {:style {:position         :relative
                 :width            "32px"
                 :height           "32px"
                 :background       (if black? :white :black)
                 :color            (if black? :black :white)
                 :font-size        "32px"}}
   piece
   (when (:is-over? @drag-state)
     [:div {:style {:width            "32px"
                    :height           "32px"
                    :position         :absolute
                    :z-index          "1"
                    :opacity          "0.5"
                    :background-color :red}}])
   (when (:can-drop? @drag-state)
     [:div {:style {:width            "32px"
                    :height           "32px"
                    :position         :absolute
                    :z-index          "2"
                    :opacity          "0.5"
                    :background-color :green}}])])

(defn move-knight-to [position]
  (swap! state assoc :knight-position position))

(defn board-square [& {:keys [position]}]
  (let [drag-state (r/atom {})
        [x y]  position
        black? (odd? (+ x y))]
    [:div {:style {:height "12.5%"
                   :width  "12.5%"}}
     [:div {:style {:position :relative :width "32px" :height "32px"}}
      [dnd/drop-target
       :types [:knight]
       :state drag-state
       :drop #(move-knight-to position)
       :can-drop? (fn [] @(can-move-knight? position))
       :child [square
               :drag-state drag-state
               :black? black?
               :piece (when @(knight-at? position)
                        [knight])]]]]))

(defn position [i]
  [(quot i 8) (rem i 8)])

(defn board []
  [:div {:style {:width     "256px"
                 :height    "256px"
                 :display   :flex
                 :flex-wrap :wrap}}
   (for [i (range 64) :let [p (position i)]]
     ^{:key (str "square-" i)}
     [board-square :position p])])

(def context (dnd/with-drag-drop-context dnd/html5-backend board))

(defn home []
  [context])
