simple-todo
===========

Simple todo application written in scala.

Service layer in scala.

REST-service in scala with play 2.

Possibly UI with ...? ...if I lose my mind maybe with js?

master: 
- data stored in a Map 

mongodb-integration:
- data stored in mongodb
- defaults to db simple-todo and collection tasks
- defaults can be overriden with params mongo.db and tasks.collection
-- e.g. testing: play test -Ddb.mongo=simple-todo-test


in the next phase data storage will be redis?/mariadb?... or implementation for all?
