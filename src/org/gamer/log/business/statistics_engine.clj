(ns org.gamer.log.business.statistics-engine
  (:require [clojure.contrib.math :as math]
            [clojure.tools.logging :as logger]
            [org.gamer.log.data.data-loader :as loader]))


; Business method used to get top ten players
(defn top-ten []
  (let [players-list (loader/get-players)]
    (take 10 (sort-by #(% 1) > (map (fn [player] 
                                      (let [player-scores (map :score (loader/get-data :p "" "" player)) scores-count (count player-scores)]
                                        [player (float (/ (apply + player-scores) scores-count))])) players-list)))))

; Utility method used to prepare params for datalog operations
(defn evaluate-operation [server game player]
  (if (and (= server "-") (= game "-") (= player "-")) :all 
    (if (and (= server "-") (= game "-")) :p
      (if (and (= server "-") (= player "-")) :g 
        (if (and (= game "-") (= player "-")) :s
          (if (= server "-") :gp 
            (if (= game "-") :sp 
              (if (= player "-") :sg :sgp))))))))

; Business method used to get average
(defn score-avg [server game player]
  
  (let [scores-list (map :score (loader/get-data (evaluate-operation server game player) server game player)) scores-count (count scores-list)]
                                         (str (float (/ (apply + scores-list) scores-count)))))

; Business method used to get standard deviation
(defn score-sd [server game player]
  (let [scores-list (map :score (loader/get-data (evaluate-operation server game player) server game player)) scores-count (count scores-list) avg (read-string (score-avg server game player))]
    (str (math/sqrt (float (/ (apply + (map #(* (- avg %) (- avg %)) scores-list)) scores-count))))))

; Business method used to calculate stats per player
(defn scores-player [player]
  (let [games (loader/get-games)]
    (map #([% (score-avg "-" % player)]))))


; Business method used to calculate stats per game
(defn scores-game [game]
  (let [players (loader/get-players)]
    (map #([% (score-avg "-" game %)]))))