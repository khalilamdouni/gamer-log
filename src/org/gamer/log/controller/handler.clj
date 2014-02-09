(ns org.gamer.log.controller.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.tools.logging :as logger]
            [org.gamer.log.data.data-loader :as loader]
            [org.gamer.log.business.statistics-engine :as stat]
            [org.gamer.log.presentation.presenter :as presenter]
            [org.gamer.log.business.charts-generator :as charts]))

(defn load-data-request []
  (loader/load-data)
  (presenter/stat-console-view))

(defroutes app-routes
  (GET "/" [] (presenter/stat-console-view))
  (GET "/load-data" [] (load-data-request))
  (GET "/top-ten" [] (presenter/top-ten-view))
  (POST "/get-stats" [server game player] (presenter/stat-results-view server game player))
  (GET "/get-hist" [avg sd] {:status 200
                             :headers {"Content-Type" "image/png"}
                             :body (charts/gen-hist-png avg sd)})
  (GET "/get-game-players-stat" [game] {:status 200
                                        :headers {"Content-Type" "image/png"}
                                        :body (charts/gen-game-players-bar-chart game)})
  (GET "/get-player-games-stat" [player] {:status 200
                                          :headers {"Content-Type" "image/png"}
                                          :body (charts/gen-player-games-bar-chart player)})
  (GET "/get-server-games-stat" [server] {:status 200
                                          :headers {"Content-Type" "image/png"}
                                          :body (charts/gen-server-games-bar-chart server)})
  (GET "/get-game-servers-stat" [game] {:status 200
                                        :headers {"Content-Type" "image/png"}
                                        :body (charts/gen-game-servers-bar-chart game)})
  (GET "/game-players-form" [] (presenter/game-players-form-view))
  (GET "/server-games-form" [] (presenter/server-games-form-view))
  (GET "/player-games-form" [] (presenter/player-games-form-view))
  (GET "/game-servers-form" [] (presenter/game-servers-form-view))
  (POST "/game-players-result" [game] (presenter/game-players-result game))
  (POST "/server-games-result" [server] (presenter/server-games-result server))
  (POST "/player-games-result" [player] (presenter/player-games-result player))
  (POST "/game-servers-result" [game] (presenter/game-servers-result game))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
