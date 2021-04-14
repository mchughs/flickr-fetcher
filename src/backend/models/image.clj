(ns backend.models.image
  (:require [clojure.java.io :as io]
            [image-resizer.core :as resizer])
  (:import java.util.UUID
           java.awt.image.BufferedImage
           javax.imageio.ImageIO
           java.io.ByteArrayOutputStream
           java.io.ByteArrayInputStream))

(defn extract-dimensions [file]
  (let [^BufferedImage img (ImageIO/read file)]
    {:width (.getWidth img)
     :height (.getHeight img)}))

(defn- scale [^java.io.BufferedInputStream in
              {:keys [width height]}]
  (let [^BufferedImage original (ImageIO/read in)
        ^BufferedImage scaled   (resizer/force-resize original width height)
        out                     (ByteArrayOutputStream.)]
    (ImageIO/write scaled "jpg" out)
    (ByteArrayInputStream. (.toByteArray out))))

(defn download!
  "Downloads and optionally resizes an image from its `uri`.
   Returns the project path to the newly downloaded file."
  [uri {:keys [width height] :as dimensions}]
  (let [file (format "resources/downloads/%s.jpg" (java.util.UUID/randomUUID))]
    (with-open [in  (io/input-stream uri)
                out (io/output-stream file)]
      (if (and width height)
        (io/copy (scale in dimensions) out)
        (io/copy in out)))
    file))
