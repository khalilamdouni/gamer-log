(ns org.gamer.log.presentation.presenter
  (:use hiccup.core)
  (:require  [org.gamer.log.data.data-loader :as loader]
    [org.gamer.log.business.statistics-engine :as stat]))

; View method used to construc the top-ten view
(defn top-ten-view []
   (html [:ul (for [player (stat/top-ten)]
                [:li (str (player 0) ": " (player 1))])]))

; View method used to construc the stats form view
(defn stat-console-view []
  (html [:form {:action "get-stats" :method "POST"} 
         [:select (for [server (loader/get-servers)]
                    [:option {:value server} server])]
         [:select (for [game (loader/get-games)]
                    [:option {:value game} game])]
         [:select (for [players (loader/get-players)]
                    [:option {:value players} players])]
         [:input {:type "submit" :value "Calculate"} "Calculate"]]))
