(ns org.gamer.log.controller.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.tools.logging :as logger]
            [org.gamer.log.data.data-loader :as loader]
            [org.gamer.log.business.statistics-engine :as stat]
            [org.gamer.log.presentation.presenter :as presenter]))

(defn load-data-request []
  (loader/load-data)
  (presenter/stat-console-view))

(defroutes app-routes
  (GET "/" [] (presenter/stat-console-view))
  (GET "/load-data" [] (load-data-request))
  (GET "/top-ten" [] (presenter/top-ten-view))
  (POST "/get-stats" [server game player] (presenter/stat-results-view server game player))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
