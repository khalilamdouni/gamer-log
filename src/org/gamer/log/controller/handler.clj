(ns org.gamer.log.controller.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.tools.logging :as logger]
            [org.gamer.log.data.data-loader :as loader]
            [org.gamer.log.business.statistics-engine :as stat]))

(defn home []
     (logger/info "-------------CALL::home------------ ")
     "Gamer log menu!")

(defroutes app-routes
  (GET "/" [] (home))
  (GET "/load-data" [] (loader/load-data))
  (GET "/get-data" [] (loader/get-data :p "" "" "DubaX"))
  (GET "/top-ten" [] (stat/top-ten))
  (GET "/get-avg" [server game player] (stat/score-avg server game player))
  (GET "/get-sd" [server game player] (stat/score-sd server game player))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
