(ns backend.route-spec
  (:require [clojure.spec.alpha :as s]
            [utils :as utils]))

(s/def ::page-size pos-int?)
(s/def ::width pos-int?)
(s/def ::height pos-int?)

(s/def ::v1-download-request
       (s/and
         (s/keys :opt-un [::page-size
                          ::width
                          ::height])
         #(utils/xand (:width %)
                      (:height %))))