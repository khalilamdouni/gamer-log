(ns org.gamer.log.presentation.presenter
  (:use hiccup.core)
  (:require  [org.gamer.log.data.data-loader :as loader]
    [org.gamer.log.business.statistics-engine :as stat]))

; Vector containing static html menu
(def menu [:div [:a {:href "/"} "Calculate Statistics"] 
          [:span " | "] 
          [:a {:href "top-ten"} "Top ten players"]
          [:span " | "]
          [:a {:href "load-data"} "Load data to memory"]])

; Vector containing static html stat form
(defn construct-stat-form []
       [:form {:action "get-stats" :method "POST"} 
              [:select {:name "server"} (conj [:option {:value "-"} "-"] (for [server (loader/get-servers)]
                                                [:option {:value server} server]))]
              [:select {:name "game"} (conj [:option {:value "-"} "-"] (for [game (loader/get-games)]
                                      [:option {:value game} game]))]
              [:select {:name "player"} (conj [:option {:value "-"} "-"] (for [player (loader/get-players)]
                                        [:option {:value player} player]))]
              [:input {:type "submit" :value "Calculate"} ]])

; View method used to construc the top-ten view
(defn top-ten-view []
   (html [:div menu [:ul (for [player (stat/top-ten)]
                        [:li (str (player 0) ": " (player 1))])]]))



; View method used to construc the stats form view
(defn stat-console-view []
  (html [:div menu (construct-stat-form)]))

; View method used to construc the stats form view
(defn stat-results-view [server game player]
  (let [avg (stat/score-avg server game player) sd (stat/score-sd server game player)]
  (html [:div menu (construct-stat-form) 
         [:table {:border "2"} [:tr [:td {} "Average"] 
                                               [:td {} "Standard deviation"]] 
                     [:tr [:td {} avg] 
                           [:td {} sd]]]
         [:img {:src (str "get-hist?avg=" avg "&sd=" sd)}]])))


; View method used to construct player stats form view
(defn stats-player-form []
  )

; View method used to construct player stats view
(defn stats-player [player]
  )

; View method used to construct game stats form view
(defn stats-game-form []
  )

; View method used to construct game stats view
(defn stats-game [game]
  )




















