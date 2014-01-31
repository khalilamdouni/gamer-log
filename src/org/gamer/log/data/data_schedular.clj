(ns org.gamer.log.data.data-schedular
  (:require [clojure.tools.logging :as logger]
            [org.gamer.log.data.data-loader :as loader]
            [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.jobs :refer [defjob]]
            [clojurewerkz.quartzite.schedule.simple :refer [schedule repeat-forever with-interval-in-milliseconds]]))

; The job of loading data
(defjob load-data-job
  [ctx]
  (logger/info "--------------load-data-JOB::BEGIN--------------")
  (loader/load-data)
  (logger/info "--------------load-data-JOB::END--------------"))

; The schedular of the loading data job
(defn start-schedular []
  (qs/initialize)
  (qs/start)
  (let [job (j/build
              (j/of-type load-data-job)
              (j/with-identity (j/key "jobs.load.data.1")))
        trigger (t/build
                  (t/with-identity (t/key "triggers.1"))
                  (t/start-now)
                  (t/with-schedule (schedule
                                     (repeat-forever)
                                     (with-interval-in-milliseconds 10000))))]
  (qs/schedule job trigger)))