(ns rp-scoring.handler
  (:require [compojure.core :refer :all]
            [compojure.handler]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :as resp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-response]]
            [rp-scoring.db :as db]))

(defroutes app-routes
  (GET "/" [] (resp/response "Alive"))
  
  (GET "/events" {:keys [params]}
    (if (or (contains? params :user) (contains? params :repository))
      (resp/response (db/get-events params))
      {:status 400
       :body {:msg "user or repository must be provided."}}))

  (GET "/scores" []
    (resp/response (db/get-aggregate-scores db/spec)))

  (route/not-found "Not Found"))

(def app
  (wrap-reload (compojure.handler/api (wrap-json-response app-routes))))
