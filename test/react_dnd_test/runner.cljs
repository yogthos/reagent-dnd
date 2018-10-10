(ns react-dnd-test.runner
  (:require
    [doo.runner :refer-macros [doo-tests]]
    [react-dnd-test.test-core]))

(doo-tests 'react-dnd-test.test-core)
