(ns org.gamer.log.business.charts-generator
  (:use [incanter core stats charts])
  (:import (java.io ByteArrayOutputStream
                    ByteArrayInputStream)))

; Chart of normal distribution 
(defn gen-hist-png
  [avg-str sd-str]
    (let [size 1000 
          m (if (nil? avg-str)
              0
              (Double/parseDouble avg-str))
          s (if (nil? sd-str)
              1
              (Double/parseDouble sd-str))
          samp (sample-normal size
                    :mean m
                    :sd s)
          chart (histogram
                  samp
                  :title "Normal distribution of rounds"
                  :x-label (str "Rounds count = " size
                      ", AVG = " m
                      ", SD = " s))
          out-stream (ByteArrayOutputStream.)
          in-stream (do
                      (save chart out-stream)
                      (ByteArrayInputStream.
                        (.toByteArray out-stream)))]
      in-stream))

