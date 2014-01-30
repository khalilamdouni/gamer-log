(ns org.gamer.log.controller.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.tools.logging :as logger]
            [org.gamer.log.data.data-loader :as loader]))

(defn home []
     (logger/info "-------------CALL::home------------ ")
     "Gamer log menu!")

(defroutes app-routes
  (GET "/" [] (home))
  (GET "/load-data" [] (loader/load-data))
  (GET "/get-data" [] (loader/get-data))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
