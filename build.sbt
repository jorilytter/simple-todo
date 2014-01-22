name := "simple-todo"

version := "0.1"

scalaVersion := "2.10.2"

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= Seq(
  "io.spray"            %   "spray-can"             % "1.2.0",
  "io.spray"            %   "spray-routing"         % "1.2.0",
  "io.spray"            %   "spray-http"         % "1.2.0",
  "io.spray"            %   "spray-testkit"         % "1.2.0" % "test",
  "com.typesafe.akka"   %%  "akka-actor"            % "2.2.1",
  "com.typesafe.akka"   %%  "akka-testkit"          % "2.2.1" % "test",
  "org.json4s"                         %%         "json4s-native"         % "3.2.4",
  "com.typesafe"                 %% "scalalogging-slf4j" % "1.0.1",
  "org.slf4j"                         % "slf4j-api"                         % "1.7.1",
  "org.slf4j"                         % "log4j-over-slf4j"         % "1.7.1",
  "ch.qos.logback"                 % "logback-classic"         % "1.0.3"
)
