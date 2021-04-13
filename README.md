# Flicker Fetcher Challenge

## Description
  Implement and push to a GitHub repository an HTTP service that has one endpoint which downloads, resizes and stores images from the [Flickr feed](https://www.flickr.com/services/feeds/docs/photos_public/):

The solution should meet the following requirements:
- Able to specify the number of images to return, if not specified then return all the images from the Flickr feed request
- Able to specify a resize width and height
- It is sufficient to save the resized images on the local disk

## Dependencies
To run the project you will need to have the Clojure CLI installed which can be found [here](https://clojure.org/guides/deps_and_cli).

#### Backend
```sh
make backend
```

You can see the usable endpoints via Swagger3 documentation at http://localhost:3000.

#### Testing
```sh
make test-clj
```
