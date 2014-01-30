(ns org.gamer.log.data.data-loader
   (:import java.io.File)
   (:use  clojure.java.io
          [clojure.contrib.datalog :only (build-work-plan run-work-plan)]
          [clojure.contrib.datalog.rules :only (<- ?- rules-set)]
          [clojure.contrib.datalog.database :only (make-database add-tuples)]
          [clojure.contrib.datalog.util :only (*trace-datalog*)])
   (:require [clojure.contrib.datalog.database :as database]
             [org.gamer.log.config.config-manager :as config]
             [clojure.tools.logging :as logger]
             [clojure.string :as str]))

; declaring an in-memory database in which we will load all logs data
(def log-data (database/make-database 
                (relation :game-log [:server :game :player :score])
                (index :game-log :player)))

; SWAP object between file system and the database
(def data-list (atom ()))

; Data loader
(defn load-data []
  (logger/info "--------------load-data::BEGIN--------------")
  (def log-folder (file (config/get-property "gamer.archive.path")))
  (doseq [server-folder (.listFiles log-folder)]
    (doseq [game-folder (.listFiles server-folder)]
      (doseq [round-file (.listFiles game-folder)]
        (logger/info (str "------------load-data:: SERVER:" 
                          (.getName server-folder) " - GAME:" 
                          (.getName game-folder) " - ROUND:" 
                          (.getName round-file)))
        (dorun (map #(swap! data-list conj [:game-log 
                                         :server (.getName server-folder) 
                                         :game (.getName game-folder) 
                                         :player ((str/split % #":") 0) 
                                         :score ((str/split % #":") 1)]) 
                 (with-open [r (reader round-file)](doall (line-seq r)))))
        )))
  (logger/info "--------------load-data::END--------------")
  "LOADED")

(defn get-data []
  (def db (apply database/add-tuples log-data @data-list))
  (def rules (rules-set (<- (:data :server ?x :game ?y :player ?z :score ?s) 
                            (:game-log :server ?x :game ?y :player ?z :score ?s))))
  (def wp-1 (build-work-plan rules (?- :data :server '??x :game ?y :player ?z :score ?s) ))
  (run-work-plan wp-1 db {'??x "jorah"})
)
