(ns backend.client
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
            [backend.models.image :as image]))

(def ^:private base-url "https://www.flickr.com/services/feeds/photos_public.gne")

(defn- get-flickr []
  (client/get base-url
    {:accept :json
     :query-params {"format" "json"
                    "nojsoncallback" "true"}}))

(defn- image-data->urls [image-data]
  (->> image-data
       (map (comp #(re-find #"https:\/\/live\.staticflickr\.com\/.*_o_.*\.jpg" %) ;; crawl the page content for a downloadble image link
                  slurp
                  #(str % "sizes/o/")
                  :link)) ;; page links, not image links
       (remove nil?)))

(defn- get-desired-img-urls
  ;; NOTE to be sure to satisfy the number of desired images we need to make multiple calls to Flickr. I don't think they return unique images eachtime though.
  "Tries to get as many images that the user asked for as possible."
  [n all-images]
  (loop [number-still-needed (min n (count all-images)) ;; we can't get more than the api returned to us
         images              all-images
         found-urls          '()]
    (if (or (zero? number-still-needed) ;; we got all we wanted
            (zero? (count images))) ;; we got as many as possible
      found-urls
      (let [new-found-urls (->> images
                                (take number-still-needed)
                                image-data->urls)
            new-found-count (count new-found-urls)
            remaining (- n new-found-count (count found-urls))]
        (recur remaining
               (drop new-found-count images)
               (concat found-urls new-found-urls))))))

(defn- get-img-urls
  "Takes nilable `n`, the number of desired images."
  [n]
  (let [image-data (-> (get-flickr)
                       :body
                       (json/parse-string true)
                       :items)]
    (if-not n
      (image-data->urls image-data)
      (get-desired-img-urls n image-data))))

(defn download-imgs! [page-size dimensions]
  (->> page-size
       get-img-urls
       (map #(image/download! % dimensions))))
