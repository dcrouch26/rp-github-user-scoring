(ns rp-scoring.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [rp-scoring.handler :refer :all]
            [rp-scoring.db :as db]
            [clojure.data.json :refer [read-json]]))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Alive"))))

  (testing "events"
    (with-redefs [db/get-events (fn [p] p)]
      (testing "no user or repository"
           (let [response (app (mock/request :get "/events"))]
             (is (= (:status response) 400))
             (is (not (nil? (:body response))))))
     (testing "user"
           (let [response (app (mock/request :get "/events?user=A"))]
             (is (= (:status response) 200))
             (is (= (read-json (:body response)) {:user "A"}))))
     (testing "repository"
           (let [response (app (mock/request :get "/events?repository=Repo"))]
             (is (= (:status response) 200))
             (is (= (read-json (:body response)) {:repository "Repo"}))))
     (testing "both"
           (let [response (app (mock/request :get "/events?repository=Repo&user=A"))]
             (is (= (:status response) 200))
             (is (= (read-json (:body response)) {:user "A" :repository "Repo"})))))

    (testing "not-found route"
      (let [response (app (mock/request :get "/nonroute"))]
        (is (= (:status response) 404))))))
