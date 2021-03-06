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

; db object, materialized as clojure atom (to be mutable)
(def db (atom ()))

; list of servers, games and players (to be ready for presentation)
(def servers (atom #{}))
(def games (atom #{}))
(def players (atom #{}))

; Data loader
(defn load-data []
  (logger/info "--------------load-data::BEGIN--------------")
  ; reset all lists
  (try (do 
           (reset! servers #{})
           (reset! games #{})
           (reset! players #{})
           (def data-list (atom ()))
           (def data-size (atom 0))
           (def error-size (atom 0))
           (def log-folder (file (config/get-property "gamer.archive.path")))
           (if (.isDirectory log-folder)   
             (doseq [server-folder (.listFiles log-folder)]
               (if (.isDirectory server-folder) 
                 (do (if (not (contains? @servers (.getName server-folder))) (swap! servers conj (.getName server-folder)))
                   (doseq [game-folder (.listFiles server-folder)]
                     (if (.isDirectory game-folder)
                       (do (if (not (contains? @games (.getName game-folder))) (swap! games conj (.getName game-folder)))
                         (doseq [round-file (.listFiles game-folder)]
                          (if (.isFile round-file)
                            (do 
                              (logger/info (str "------------load-data:: SERVER:" 
                                                (.getName server-folder) " - GAME:" 
                                                (.getName game-folder) " - ROUND:" 
                                                (.getName round-file)))
                              (dorun (map (fn [line] 
                                            (try (do 
                                                   (swap! data-size inc)
                                                   (swap! data-list conj [:game-log 
                                                                          :server (.getName server-folder) 
                                                                          :game (.getName game-folder) 
                                                                          :player ((str/split line #":") 0) 
                                                                          :score (read-string ((str/split line #":") 1))])
                                                   (if (not (contains? @players ((str/split line #":") 0))) (swap! players conj ((str/split line #":") 0))))  
                                              (catch Exception e (do (swap! error-size inc) 
                                                                   (logger/info (str "------------load-data:: THE FILE *" (.getName round-file) "* is corrupted !")))))) 
                                          (with-open [r (reader round-file)] 
                                            (doall (line-seq r)))))))))))))))
           (logger/info (str "--------------load-data::END-------------- ERROR size : " @error-size " | DATA size : " @data-size))
           ; before loading data in databse we have to check the error rate 
           (if (< (/ @error-size @data-size) 1/5) 
             (reset! db (apply database/add-tuples log-data @data-list)) 
             (do (reset! servers #{}) (reset! games #{}) (reset! players #{})))) 
    (catch Exception e (logger/info "---------------------- ERROR IN LOADING DATA ----------------------"))))


; Querying database using datalog language
(defn get-data [request server game player]
  (def rules (rules-set (<- (:data :server ?x :game ?y :player ?z :score ?s) 
                            (:game-log :server ?x :game ?y :player ?z :score ?s))))
  
  (def requests {:sgp (build-work-plan rules (?- :data :server '??x :game '??y :player '??z :score ?s)),
                 :s (build-work-plan rules (?- :data :server '??x :game ?y :player ?z :score ?s)),
                 :g (build-work-plan rules (?- :data :server ?x :game '??y :player ?z :score ?s)),
                 :p (build-work-plan rules (?- :data :server ?x :game ?y :player '??z :score ?s)),
                 :sg (build-work-plan rules (?- :data :server '??x :game '??y :player ?z :score ?s)),
                 :sp (build-work-plan rules (?- :data :server '??x :game ?y :player '??z :score ?s)),
                 :gp (build-work-plan rules (?- :data :server ?x :game '??y :player '??z :score ?s)),
                 :all (build-work-plan rules (?- :data :server ?x :game ?y :player ?z :score ?s))})
  
  (def params {:sgp {'??x server, '??y game, '??z player},
               :s {'??x server},
               :g {'??y game},
               :p {'??z player},
               :sg {'??x server, '??y game},
               :sp {'??x server, '??z player},
               :gp {'??y game, '??z player},
               :all {}})
  
  (run-work-plan (request requests) @db (request params)))

; Data method which return list of servers
(defn get-servers []
  @servers)

; Data method which return list of games
(defn get-games []
  @games)

; Data method which return list of players
(defn get-players []
  @players)
