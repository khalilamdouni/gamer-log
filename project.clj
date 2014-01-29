(defproject compojure-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.3"]
                 [log4j "1.2.16"]
                 [compojure "1.1.6"]
                 [org.clojure/java.jdbc "0.0.6"]
                 [org.clojure/clojure-contrib "1.2.0"]]
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler org.gamer.log.controller.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
