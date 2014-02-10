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
  (try (let [scores-list (map :score (loader/get-data (evaluate-operation server game player) server game player)) scores-count (count scores-list)]
                                              (str (float (/ (apply + scores-list) scores-count))))
    (catch Exception e "0")))

; Business method used to get standard deviation
(defn score-sd [server game player]
  (try (let [scores-list (map :score (loader/get-data (evaluate-operation server game player) server game player)) scores-count (count scores-list) avg (read-string (score-avg server game player))]
         (str (math/sqrt (float (/ (apply + (map #(* (- avg %) (- avg %)) scores-list)) scores-count)))))
    (catch Exception e "0")))

; Business method used to calculate stats per player
(defn scores-player-over-games [player]
  (let [games (loader/get-games)]
    (filter (fn [gitem] 
              (> (gitem 1) 0)) (map (fn [game] 
                                      [game (read-string (score-avg "-" game player))]) games))))

; Business method used to calculate stats per game
(defn scores-game-over-players [game]
  (let [players (loader/get-players)]
    (take 20 (sort-by #(% 1) > (filter (fn [pitem] 
                                   (> (pitem 1) 0)) (map (fn [player] 
                                                           [player (read-string (score-avg "-" game player))]) players))))))

; Business method used to calculate stats per server
(defn scores-server-over-games [server]
  (let [games (loader/get-games)]
    (filter (fn [gitem] 
              (> (gitem 1) 0)) (map (fn [game] 
                                      [game (read-string (score-avg server game "-"))]) games))))

; Business method used to calculate stats per game over servers
(defn scores-game-over-servers [game]
  (let [servers (loader/get-servers)]
    (filter (fn [sitem] 
              (> (sitem 1) 0)) (map (fn [server] 
                                      [server (read-string (score-avg server game "-"))]) servers))))