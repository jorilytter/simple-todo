name := "simple-todo"

version := "0.1"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.3.7",
  jdbc,
  anorm,
  cache
)     

play.Project.playScalaSettings
