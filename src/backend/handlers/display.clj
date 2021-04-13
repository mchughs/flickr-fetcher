(ns backend.handlers.display
  (:require [clojure.java.io :as io]
            [utils           :as utils]))

(defn handler [request]
  (let [id (-> request
               (get-in [:parameters :path :id])
               utils/str->uuid)]
    (if (uuid? id)
      {:status 200
       :headers {"Content-Type" "image/jpg"}
       :body (-> (format "downloads/%s.jpg" id)
                 io/resource
                 io/input-stream)}
      {:status 404
       :body (format "Not a valid ID: %s" id)})))
