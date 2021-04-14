(ns handler-test
  (:require [ring.mock.request :as mock]
            [clojure.test :refer :all]
            [backend.handlers.download :as download]
            [backend.client :as client]))

(def my-urls ["url/1" "url/2" "url/3" "url/4" "url/5"])

(deftest download-tests
  (testing "Test that download returns the right json body."
    (with-redefs [client/download-imgs! (constantly my-urls)]
      ;;NOTE ring mock isn't a great choice here actually because the way requests are formed doesn't conform to what reitit expects.
      (let [request (->> (mock/request :get "/api/v1/download")
                         (merge {:parameters {:query {:page-size 5}}}))
            response (download/handler request)]
        (is (= (select-keys response [:status :body])
               {:status  200
                :body    {:number-requested  5
                          :number-downloaded 5
                          :download-paths    my-urls}})))
      (let [request (->> (mock/request :get "/api/v1/download")
                         (merge {:parameters {:query {:page-size 5
                                                      :width 100
                                                      :height 100}}}))
            response (download/handler request)]
        (is (= (select-keys response [:status :body])
               {:status  200
                :body    {:number-requested  5
                          :number-downloaded 5
                          :download-paths    my-urls}}))))))
