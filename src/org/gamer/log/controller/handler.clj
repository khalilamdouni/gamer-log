(ns org.gamer.log.controller.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.tools.logging :as logger]
            [org.gamer.log.data.data-loader :as loader]
            [org.gamer.log.business.statistics-engine :as stat]
            [org.gamer.log.presentation.presenter :as presenter]))

(defn home []
     (logger/info "-------------CALL::home------------ ")
     "Gamer log menu!")

(defroutes app-routes
  (GET "/" [] (home))
  (GET "/load-data" [] (loader/load-data))
  (GET "/top-ten" [] (presenter/top-ten-view))
  (GET "/stats" [] (presenter/stat-console-view))
  (GET "/get-avg" [server game player] (stat/score-avg server game player))
  (GET "/get-sd" [server game player] (stat/score-sd server game player))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
