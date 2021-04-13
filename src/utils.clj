(ns utils
  (:require [clojure.tools.logging :as log])
  (:import java.util.UUID))

(defn str->uuid [s]
  (if (uuid? s)
    s
    (try
      (UUID/fromString s)
      (catch Exception _
        (log/infof "Not a UUID: %s" (pr-str s))))))

(defn xand [& preds]
  (boolean
    (or (every? identity preds)
        (every? not preds))))
