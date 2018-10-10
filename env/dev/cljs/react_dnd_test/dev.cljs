(ns ^:figwheel-no-load react-dnd-test.dev
  (:require
    [react-dnd-test.test-page :as test-page]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(test-page/init!)
