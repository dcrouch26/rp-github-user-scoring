(ns rp-scoring.common
  (:require [aero.core :refer [read-config]]
            [clojure.java.io :as io]))

(def app-config (read-config (io/resource "config.edn")))

(println app-config)
