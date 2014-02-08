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
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
