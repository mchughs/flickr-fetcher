(ns backend.handlers.fetch
  (:require [clojure.java.io :as io]
            [backend.models.image :as image]))

(def dir (io/file (io/resource "downloads")))

(defn handler [_request]
  {:status 200
   :body {:files (->> dir
                      file-seq
                      (remove #(.isDirectory %))
                      (map #(merge {:name (.getName %)}
                                   (image/extract-dimensions %))))}})
