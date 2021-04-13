(ns backend.handlers.fetch
  (:require [backend.client :as client]))

(defn handler
  [{{{:keys [page-size width height]} :query} :parameters}]
  (let [download-paths (client/download-imgs! page-size {:width width :height height})]
    {:status 200
     :body {:number-requested  page-size
            :number-downloaded (count download-paths)
            :download-paths    download-paths}}))
