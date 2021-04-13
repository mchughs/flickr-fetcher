(ns backend.server
  "Technical Challenge

  Flickr fetcher Implement and push to a GitHub repository an HTTP service that has one endpoint which downloads,
  resizes and stores images from the Flickr feed:
  (https://www.flickr.com/services/feeds/docs/photos_public/).
  Your solution should meet the following requirements:
  - Able to specify the number of images to return, if not specified then return all the images from the Flickr feed request
  - Able to specify a resize width and height
  - It is sufficient to save the resized images on the local disk

  You are very welcome to use libraries and 3rd party services, as long as they are not all-in-one solutions to these requirements.Please include a README explaining how to use your solution along with any explanation you wish to provide.

  Any questions at all, let me know!"
  (:require [backend.routes.swagger            :as routes.swagger]
            [backend.route-spec                :as spec]
            [backend.handlers.fetch            :as fetch]
            [backend.handlers.display          :as display]
            [muuntaja.core                     :as m]
            [reitit.coercion.spec]
            [reitit.ring                       :as ring]
            [reitit.swagger-ui                 :as swagger-ui]
            ;; Middleware libs
            [reitit.ring.coercion              :as coercion]
            [reitit.ring.middleware.muuntaja   :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.swagger                    :as swagger]
            [ring.middleware.keyword-params    :as keyword-params]
            [ring.middleware.params            :as params]))

(def router-config
  {:data {:coercion   reitit.coercion.spec/coercion
          :muuntaja   m/instance
          :middleware [swagger/swagger-feature
                       ;; query-params & form-params
                       parameters/parameters-middleware
                       ;; Middleware to parse urlencoded parameters from the query string
                       params/wrap-params
                       ;; Middleware that converts the any string keys in the :params map to keywords.
                       keyword-params/wrap-keyword-params
                       ;; Middleware for content-negotiation, request and response formatting.
                       muuntaja/format-middleware
                       ;; coercing response bodys
                       coercion/coerce-response-middleware
                       ;; coercing request parameters
                       coercion/coerce-request-middleware]}})

(def app
  (ring/ring-handler
    (ring/router
      [["/status" {:no-doc true
                   :get (fn [_] {:status 200
                                 :body "ok\n"})}]
       routes.swagger/swag
       ["/api/v1"
        ["/fetch" {:name :v1/fetch
                   :summary "Fetch photos from Flickr"
                   :description "Photos will download directly into the directory `resources/downloads/` found at the project root."
                   :coercion reitit.coercion.spec/coercion
                   :parameters {:query ::spec/v1-fetch-request}
                   :get fetch/handler}]
        ["/display/:id" {:name :v1/display
                         :summary "Display a photo saved on disk"
                         :description "Images displayed here are automatically scaled up to the size of the window.
                                       If you want to see an accurate size of the image, run the request in a separate tab."
                         :swagger {:produces ["image/jpg"]}
                         :parameters {:path {:id uuid?}}
                         :get display/handler}]]]
        ;; TODO Add an endpoint for droping the downloads folder contents.
        ;; TODO Add an endpoint for getting the names of the files in the downloads folder.
      router-config)
    (ring/routes
      (swagger-ui/create-swagger-ui-handler {:path "/"})
      (ring/create-default-handler))))
