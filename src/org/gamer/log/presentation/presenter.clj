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

(defn no-data-view []
  (html [:div menu [:b {:style "color:red"} "No data available, please check log files"]]))

; View method used to construc the top-ten view
(defn top-ten-view []
  (if (or (= (count (loader/get-servers)) 0) (= (count (loader/get-games)) 0) (= (count (loader/get-players)) 0)) (no-data-view)    
    (html [:div menu [:ul (for [player (stat/top-ten)]
                        [:li (str (player 0) ": " (player 1))])]]))
)

; View method used to construc the stats form view
(defn stat-console-view []
    (if (or (= (count (loader/get-servers)) 0) (= (count (loader/get-games)) 0) (= (count (loader/get-players)) 0)) (no-data-view)    
      (html [:div menu (construct-stat-form)])))

; View method used to construc the stats form view
(defn stat-results-view [server game player]
  (if (or (= (count (loader/get-servers)) 0) (= (count (loader/get-games)) 0) (= (count (loader/get-players)) 0)) (no-data-view)    
    (let [avg (stat/score-avg server game player) sd (stat/score-sd server game player)]
      (html [:div menu [:div {:style "float:left"}  (construct-stat-form)  
                        (if (and (= avg "0") (= sd "0")) [:b {:style "color:red"} "No data for this combination!"] 
                          [:table {:border "2"} [:tr [:td {} "Average"] 
                                 [:td {} "Standard deviation"]] 
                                        [:tr [:td {} avg] 
                                         [:td {} sd]]])]
             (if (not (and (= avg "0") (= sd "0"))) 
               [:div {:style "float:left;margin-left:10%"} 
                [:img {:src (str "get-hist?avg=" avg "&sd=" sd) :border "1px" :style "border-color:firebrick"}]])]))))


(def stat-requests {:game-players {:data-funtion loader/get-games, :item-name "game", :result-action "game-players-result", :chart-url "get-game-players-stat?game="},
                    :server-games {:data-funtion loader/get-servers, :item-name "server", :result-action "server-games-result", :chart-url "get-server-games-stat?server="},
                    :player-games {:data-funtion loader/get-players, :item-name "player", :result-action "player-games-result", :chart-url "get-player-games-stat?player="},
                    :game-servers {:data-funtion loader/get-games, :item-name "game", :result-action "game-servers-result", :chart-url "get-game-servers-stat?game="}})

; View method used to construct stats form 
(defn construct-form [stat-request]
  [:form {:action (:result-action (stat-request stat-requests)) :method "POST"} 
   [:br]
   [:span (str "Select the " (:item-name (stat-request stat-requests)) ": ")]
   [:select {:name (:item-name (stat-request stat-requests))} (for [item ((:data-funtion (stat-request stat-requests)))]
                                                                [:option {:value item} item])]
   [:input {:type "submit" :value "Statistics"}]])

; View method used to return stats form view
(defn get-form-view [stat-request]
  (if (or (= (count (loader/get-servers)) 0) (= (count (loader/get-games)) 0) (= (count (loader/get-players)) 0)) (no-data-view)    
    (html [:div menu (construct-form stat-request)])))

; View method used to return stats results view
(defn get-stat-result [item stat-request]
  (if (or (= (count (loader/get-servers)) 0) (= (count (loader/get-games)) 0) (= (count (loader/get-players)) 0)) (no-data-view)    
    (html [:div menu [:div {:style "float:left"} (construct-form stat-request)]
           [:div {:style "float:left;margin-left:15%"} [:img {:src (str (:chart-url (stat-request stat-requests)) item) :border "1px" :style "border-color:firebrick"}]]])))
