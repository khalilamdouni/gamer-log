(ns org.gamer.log.config.config-manager
  (:use [clojure.java.io :only (reader resource)])
  (:require [clojure.string :as str])
  (:import (java.util Properties)))

(defn load-properties [src]
  (with-open [rdr (reader src)]
    (doto (Properties.)
      (.load rdr))))

(defn get-property
  [property-name]
  (.get (load-properties (resource "gamer-log.properties")) property-name))
