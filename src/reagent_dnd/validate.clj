(ns reagent-dnd.validate)

(defmacro validate-args-macro
  "if goog.DEBUG is true then validate the args, otherwise replace the validation code with true
  for production builds which the {:pre ...} will be happy with"
  [args-desc args component-name]
  `(if-not ~(vary-meta 'js/goog.DEBUG assoc :tag 'boolean)
     true
     (reagent-dnd.validate/validate-args (reagent-dnd.validate/extract-arg-data ~args-desc) ~args ~component-name)))
