(ns backend.handlers.fetch
  (:require [clojure.java.io :as io]
            [backend.models.image :as image]))

(defn handler [_request]
  (let [dir (io/file (io/resource "downloads"))
        files (->> dir
                   file-seq
                   (remove #(.isDirectory %))
                   (map #(merge {:name (.getName %)}
                                (image/extract-dimensions %))))]
    {:status 200
     :body {:files files}}))
