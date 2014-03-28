name := "simple-todo"

version := "0.1"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "2.0.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "mysql" % "mysql-connector-java" % "5.1.29",
  "c3p0" % "c3p0" % "0.9.1.2"
)     

play.Project.playScalaSettings

ScoverageSbtPlugin.instrumentSettings

