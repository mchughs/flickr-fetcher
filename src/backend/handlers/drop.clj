(ns backend.handlers.drop
  (:require [clojure.java.io :as io]))

;; Would normally be moved into a configuration
(def dir (io/file (io/resource "downloads")))

(defn handler [_request]
  (let [files (->> dir
                   file-seq
                   (remove #(.isDirectory %)))]
    (if (empty? files)
      {:status 410 ;; Gone
       :body "No files to delete."}
      (if-let [_all-deleted? (->> files
                                  (map io/delete-file)
                                  (every? identity))]
        {:status 200
         :body {:deleted-files files}}
        {:status 409 ;; Conflict
         :body "Couldn't delete some files in the directory."}))))
