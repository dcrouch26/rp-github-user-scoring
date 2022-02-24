(ns rp-scoring.core
  (:gen-class)
  (:require [rp-scoring.kafka :as kafka]
            [clojure.tools.logging :as log]
            [rp-scoring.common :refer [app-config]]
            [rp-scoring.db :as db]
            [rp-scoring.handler :as h]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojure.core.async :as async])
  (:import [org.apache.kafka.common.errors WakeupException]))

(def repos ["aero" "async" "jackdaw" "ring" "compojure"])

(def users ["Jake" "Sarah" "Sam" "Amber" "George" "Becky"])

(def events ["Push" "PullRequestReviewComment" "Watch" "Create" "Fork" "Release"])

(defn github-event
  []
  {:type (rand-nth events)
   :user (rand-nth users)
   :repo (rand-nth repos)})

(defn write-sql [msgs]
  (println "Writing" (count msgs) "event(s)")
  (doseq [m msgs]
    (db/insert-event db/spec (:value m))))

(defn- uninitialized?
  []
  (kafka/topic-exists? (:topic app-config)))

(defn- initialize
  []
  (kafka/initialize)
  (db/initialize))

(defn- delete
  []  
  (kafka/delete)
  (db/delete))

(defn- purge
  []
  (delete)
  (Thread/sleep 5000)
  (initialize))

(defn- run-pipeline
  []
  (kafka/consumer-loop! (:topic app-config) write-sql)
  (kafka/produce-loop! (:topic app-config) github-event))

(defn- run-all
  []
  (if (uninitialized?) (initialize))
  (async/thread (run-jetty h/app {:port 3000}))
  (run-pipeline))

(defn -main [& args]
  (case (first args)
        "--init" (initialize)
        "--delete" (delete)
        "--purge-data" (purge) 
        "--pipeline" (run-pipeline)
        "--service" (run-jetty h/app {:port 3000})
        (run-all)))
