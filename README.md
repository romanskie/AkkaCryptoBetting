AkkaCryptoBetting
=================

AkkaCryptoBetting is a prototype application that allows users to bet against each other on rising or falling cryptocurrency prices via live betting. Each currency stands its own betting topic. Users can place or join as many bets as they want at the same time. The wining rate is paid out in the currency "scalaCoins" a completely random and non existing cryptocurrency. Besides the functionality of live betting, AkkaCryptoBetting offers live chatrooms for each betting topic as well. The project is part of the Scala computer science master course at LMU University Munich.

Technologies Server:
====================
* Play! Framework (https://github.com/playframework/playframework)
* Akka Actors and Akka Streams (https://github.com/akka/akka)
* Circe (via Play-Circe) as JSON library (https://github.com/jilen/play-circe)
* Slick for async non blocking DB (PostgreSQL) access (https://github.com/slick/slick)
* Macwire for compile time DI (https://github.com/adamw/macwire)

Technologies Client:
===================
* Scala-js (https://github.com/scala-js/scala-js)
* Monadic-HTML (https://github.com/OlivierBlanvillain/monadic-html)


Usage:
=====
From inside the root directory execute "sbt run". The server is listening on [port 9000](http://localhost:9000)
