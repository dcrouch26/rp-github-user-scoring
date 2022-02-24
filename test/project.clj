(defproject rp-scoring "0.1.0-SNAPSHOT"
  :description "Web application and kafka functionality for github metricing"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/core.async "1.5.648"]
                 [ring "1.7.1"]
                 [compojure "1.6.2"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.1"]
                 [fundingcircle/jackdaw "0.9.3"]
                 [org.apache.kafka/kafka-streams "2.1.0"]
                 [aero "1.1.6"]
                 [org.clojure/java.jdbc "0.7.9"]
                 [org.postgresql/postgresql "42.2.5"]
                 [com.layerware/hugsql "0.5.1"]
                 [com.fzakaria/slf4j-timbre "0.3.21"]
                 [org.clojure/data.json "2.4.0"]]
  :repositories {"confluent" {:url "https://packages.confluent.io/maven/"}
                 "local" {:url "file:maven_repository"}}
  :plugins [[lein-ring "0.12.5"]
            [lein-dotenv "RELEASE"]]
  :resource-paths ["resources"]
  :extra-paths ["resources"]
  :uberjar-name "rp-scoring.jar"
  :main rp-scoring.core
  :aot [rp-scoring.core]
  :ring {:handler rp-scoring.handler/app
         :reload-paths ["src" "resources"]}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
