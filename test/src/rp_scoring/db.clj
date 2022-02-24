(ns rp-scoring.db
  (:require [hugsql.core :as hugsql]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as j]
            [rp-scoring.common :refer [app-config]]))

(def spec (:db-spec app-config))

(hugsql/def-db-fns (io/resource "scoring.sql"))

(defn delete []
  (drop-events-table spec)
  (drop-scores-table spec))

(defn initialize []
  (create-events-table spec)
  (create-scores-table spec)
  (populate-scores spec))

(defn get-events [{:keys [user repository] :as params}]
  (if user
    (if repository
      (get-user-repo-events spec params)
      (get-user-events spec params))
    (if repository
      (get-repo-events spec params))))
