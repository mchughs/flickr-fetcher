(ns handler-test
  (:require [ring.mock.request :as mock]
            [clojure.test :refer :all]
            [clojure.java.io :as io]
            [backend.handlers.fetch :as fetch]
            [backend.handlers.download :as download]
            [backend.handlers.drop :as drop]
            [backend.client :as client]))

(def my-urls ["url/1" "url/2" "url/3" "url/4" "url/5"])

(def target-dir (io/file "tmp"))

(deftest download-tests
  (with-redefs [client/download-imgs! (fn [_ _]
                                        (io/copy (io/file "resources/example.jpg")
                                                 (io/file "tmp/example.jpg"))
                                        my-urls)
                fetch/dir target-dir
                drop/dir target-dir]
    (testing "Test that download returns the right json body."
      ;;NOTE ring mock isn't a great choice here actually because the way requests are formed doesn't conform to what reitit expects.
      (let [request (->> (mock/request :get "/api/v1/download")
                         (merge {:parameters {:query {:page-size 5
                                                      :width 100
                                                      :height 100}}}))
            response (download/handler request)]
        (is (= (select-keys response [:status :body])
               {:status  200
                :body    {:number-requested  5
                          :number-downloaded 5
                          :download-paths    my-urls}}))))
    (testing "Test that fetch retrieves the downloaded file."
      (let [request (mock/request :get "/api/v1/fetch")
            response (fetch/handler request)]
        (is (= (select-keys response [:status :body])
               {:status  200
                :body    {:files '({:name "example.jpg",
                                    :width 800,
                                    :height 800})}}))))
    (testing "Test that drop deletes the downloaded file."
      (let [request (mock/request :get "/api/v1/drop")
            response (drop/handler request)]
        (is (= (select-keys response [:status])
               {:status 200}))
        (is (empty? (.listFiles target-dir)))))))
