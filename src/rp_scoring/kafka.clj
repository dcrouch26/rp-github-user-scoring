(ns rp-scoring.kafka
  (:require [jackdaw.admin :as ja]
            [jackdaw.client :as jc]
            [jackdaw.serdes :refer [string-serde edn-serde]]
            [clojure.core.async :as async]
            [clojure.tools.logging :as log]
            [rp-scoring.common :refer [app-config]])
  (:import [org.apache.kafka.common.errors WakeupException]))

(defn consumer-config
  [bootstrap group]
  {"bootstrap.servers" bootstrap
   "group.id" group
   "auto.offset.reset" "earliest"
   "enable.auto.commit" "false"})

(defn topic-config
  [topic-name]
  {:topic-name topic-name
   :partition-count 1
   :replication-factor 1
   :topic-config {}
   :key-serde (string-serde)
   :value-serde (edn-serde)})

(defn produce-loop! [topic-name gen-fn]
  (let [topic (topic-config topic-name)]
    (with-open [producer (jc/producer {"bootstrap.servers" (:bootstrap app-config)} topic)]
      (loop []
        (let [data (gen-fn)
              _ (println "Producing: " data)]
          @(jc/produce! producer topic data)
          (Thread/sleep (rand-int 5000)))
        (recur)))))

(defn consumer-loop!
  [topic-name process-fn]
  (let [topic-config    (topic-config topic-name)
        consumer-config (consumer-config (:bootstrap app-config) "1")
        consumer (jc/subscribed-consumer consumer-config [topic-config])]
    (.addShutdownHook (Runtime/getRuntime) (Thread. #(.wakeup consumer)))
    (async/thread
      (try 
        (loop []
          (let [records (jc/poll consumer 5000)]
            (when (seq records)
              (process-fn records)
              (.commitSync consumer))
            (recur)))
        (catch WakeupException e)
        (finally (.close consumer))))))

(defn topic-exists? [topic]
  (let [exists (some #(= (:topic-name %) topic)
                (ja/list-topics
                 (ja/->AdminClient {"bootstrap.servers" (:bootstrap app-config)})))]
    (log/info topic " " exists)
    exists))

(defn delete []
  (let [client (ja/->AdminClient {"bootstrap.servers" (:bootstrap app-config)})
        topic (topic-config (:topic app-config))]
    (ja/delete-topics! client [topic])
    (log/info topic "deleted.")))

(defn initialize []
  (let [client (ja/->AdminClient {"bootstrap.servers" (:bootstrap app-config)})
        topic (topic-config (:topic app-config))]
    (ja/create-topics! client [topic])
    (log/info topic "created.")))


