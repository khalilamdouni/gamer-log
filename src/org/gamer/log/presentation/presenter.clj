(ns org.gamer.log.presentation.presenter
  (:use hiccup.core)
  (:require  [org.gamer.log.data.data-loader :as loader]
    [org.gamer.log.business.statistics-engine :as stat]))

; Vector containing static html menu
(def menu [:div [:a {:href "/"} "Calculate Statistics"] 
          [:span {:style "color:firebrick"} " | "] 
          [:a {:href "top-ten"} "Top ten players"]
          [:span {:style "color:firebrick"} " | "] 
          [:a {:href "load-data"} "Load data to memory"]
          [:span {:style "color:firebrick"} " | "] 
          [:a {:href "game-players-form"} "Stats game over players"]
          [:span {:style "color:firebrick"} " | "] 
          [:a {:href "server-games-form"} "Stats server over games"]
          [:span {:style "color:firebrick"} " | "] 
          [:a {:href "player-games-form"} "Stats player over games"]
          [:span {:style "color:firebrick"} " | "] 
          [:a {:href "game-servers-form"} "Stats game over servers"]
          [:br]
          [:hr {:style "width:100%;color:firebrick;background-color:firebrick;height:3px;"}]
          [:br][:br]])

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
    (html [:div menu [:div {:style "float:left"}  (construct-stat-form)  
                      (if (and (= avg "0") (= sd "0")) [:b {:style "color:red"} "No data for this combination!"] 
                        [:table {:border "2"} [:tr [:td {} "Average"] 
                               [:td {} "Standard deviation"]] 
                                      [:tr [:td {} avg] 
                                       [:td {} sd]]])]
           (if (not (and (= avg "0") (= sd "0"))) 
             [:div {:style "float:left;margin-left:10%"} 
              [:img {:src (str "get-hist?avg=" avg "&sd=" sd) :border "1px" :style "border-color:firebrick"}]])])))

; View method used to construct game over players form 
(defn construct-game-players-form []
  [:form {:action "game-players-result" :method "POST"} 
   [:br]
   [:span "Select the game: "]
   [:select {:name "game"} (for [game (loader/get-games)]
                             [:option {:value game} game])]
   [:input {:type "submit" :value "Statistics"}]])

; View method used to construct server over games form
(defn construct-server-games-form []
  [:form {:action "server-games-result" :method "POST"} 
   [:br]
   [:span "Select the server: "]
   [:select {:name "server"} (for [server (loader/get-servers)]
                               [:option {:value server} server])]
   [:input {:type "submit" :value "Statistics"}]])

; View method used to construct player over games form
(defn construct-player-games-form []
  [:form {:action "player-games-result" :method "POST"} 
   [:br]
   [:span "Select the player: "]
   [:select {:name "player"} (for [player (loader/get-players)]
                               [:option {:value player} player])]
   [:input {:type "submit" :value "Statistics"}]])

; View method used to construct game over servers form
(defn construct-game-servers-form []
  [:form {:action "game-servers-result" :method "POST"} 
   [:br]
   [:span "Select the game: "]
   [:select {:name "game"} (for [game (loader/get-games)]
                             [:option {:value game} game])]
   [:input {:type "submit" :value "Statistics"}]])

; View method used to construct game over players form view
(defn game-players-form-view []
    (html [:div menu (construct-game-players-form)]))

; View method used to construct server over games form view
(defn server-games-form-view []
    (html [:div menu (construct-server-games-form)]))

; View method used to construct player over games form view
(defn player-games-form-view []
    (html [:div menu (construct-player-games-form)]))

; View method used to construct game over servers form view
(defn game-servers-form-view []
    (html [:div menu (construct-game-servers-form)]))


; View method used to construct game over players results view
(defn game-players-result [game]
  (html [:div menu [:div {:style "float:left"} (construct-game-players-form)]
         [:div {:style "float:left;margin-left:15%"} [:img {:src (str "get-game-players-stat?game=" game) :border "1px" :style "border-color:firebrick"}]]]))

; View method used to construct server over games results view
(defn server-games-result [server]
  (html [:div menu [:div {:style "float:left"} (construct-server-games-form)]
         [:div {:style "float:left;margin-left:15%"} [:img {:src (str "get-server-games-stat?server=" server) :border "1px" :style "border-color:firebrick"}]]]))

; View method used to construct player over games results view
(defn player-games-result [player]
  (html [:div menu [:div {:style "float:left"} (construct-player-games-form)]
         [:div {:style "float:left;margin-left:15%"} [:img {:src (str "get-player-games-stat?player=" player) :border "1px" :style "border-color:firebrick"}]]]))

; View method used to construct game over servers results view
(defn game-servers-result [game]
  (html [:div menu [:div {:style "float:left"} (construct-game-servers-form)]
         [:div {:style "float:left;margin-left:15%"} [:img {:src (str "get-game-servers-stat?game=" game) :border "1px" :style "border-color:firebrick"}]]]))
