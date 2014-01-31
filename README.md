# Gamer-log

The GamerLog project is an engine which collects log files (containing gamers and scores) to put them in a WEB portal,
The platform provides some analysis functionalities on log datas,
The plaform is developed with [Clojure][1] language with the WEB API [Compojure][2]

[1]: http://clojure.org
[2]: http://en.wikibooks.org/wiki/Compojure

## Namespaces 

- The [data-loader][3] namespace (/src/org/gamer/log/data/data_loader.clj): this namespace is responsible for loading data from the file system, storing logs in memory and querrying the memory databse (using the [datalog][8] language)

- The [statistics-engine][4] namespace (/src/org/gamer/log/business/statistics_engine.clj): this namespace provides statistics operations (top-ten players, average and standard deviation of a given player across games and machines, or of a given game across players and machines, etc)

- The [pesenter][5] namespace (/src/org/gamer/log/presentation/presenter.clj): this namespace is responsible of html view creation using the hiccup framework

- The [controller][6] namespace (/src/org/gamer/log/controller/handler.clj): it's the central conroller o he application which receives and treats all client http requests

- The [config-manager][7] namespace (/src/org/gamer/log/config/config_manager.clj):  this namespace is responsible of loading properties from the gamer-log.properties (for the moment the only property we have is he path of scores logs)

[3]:https://github.com/khalilamdouni/gamer-log/blob/master/src/org/gamer/log/data/data_loader.clj
[4]:https://github.com/khalilamdouni/gamer-log/blob/master/src/org/gamer/log/business/statistics_engine.clj
[5]:https://github.com/khalilamdouni/gamer-log/blob/master/src/org/gamer/log/presentation/presenter.clj
[6]:https://github.com/khalilamdouni/gamer-log/blob/master/src/org/gamer/log/controller/handler.clj
[7]:https://github.com/khalilamdouni/gamer-log/blob/master/src/org/gamer/log/config/config_manager.clj	
[8]:http://en.wikipedia.org/wiki/Datalog

## Prerequisites

You will need [Leiningen][9] 1.7.0 or above installed.

[9]: https://github.com/technomancy/leiningen

## Frameworks 
### Datalog
To store data in memoy and facilitate querying them i used the clojure implementation Datalog (clojure.contrib.datalog), i chose this in memory db implementation for two reasons:
- it's a light implementation, (we are not obliged start a db server or install other features to use it), in our project we need just to struture datas and facilitate querying them.
- it's based on the datalog querying language which is a strong querying language used in many fields.


## Running
Steps to launch the application:
1) Change the  application log path in the log4j.properties 
2) Change the scores log path (the path in which the application will find the servers, games, players and scores)
3) Launch the following command inside the app directory 

    lein ring server

## License

Copyright © 2014 [Gamer-log][10]

[10]: https://github.com/khalilamdouni/gamer-log
