(ns backend.routes.swagger
  (:require [reitit.swagger :as swagger]))

(def swag
  ["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "Flickr Fetcher"}}
           :handler (swagger/create-swagger-handler)}}])