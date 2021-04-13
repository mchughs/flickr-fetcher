(ns utils-test
  (:require [utils :as sut]
            [clojure.test :refer :all]))

(deftest xand-test
  (testing "Logical operator xand should behave as expected."
    (are [expected actual] (= expected actual)
      true  (sut/xand true true)
      false (sut/xand false true)
      false (sut/xand true false)
      true  (sut/xand false false))))
