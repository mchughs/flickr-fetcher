(ns spec-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [backend.route-spec :as sut]))

(deftest v1-download-request-spec-test
  (testing "The spec for v1-download-request should work on proper inputs."
    (are [expected input] (= expected
                             (not= :clojure.spec.alpha/invalid
                                   (s/conform ::sut/v1-download-request input)))
      true  {}
      true  {:page-size 1}
      true  {:width 1 :height 1}
      true  {:page-size 1 :width 1 :height 1}
      false 1
      false {:width 1}
      false {:height 1}
      false {:page-size 1 :width "a"}
      false {:page-size 1 :width 1}
      false {:page-size 1 :width 1 :height "a"})))
