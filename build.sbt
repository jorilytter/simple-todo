name := "simple-todo"

version := "0.1"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

play.Project.playScalaSettings

ScoverageSbtPlugin.instrumentSettings

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")
