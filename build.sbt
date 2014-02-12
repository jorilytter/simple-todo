name := "simple-todo"

version := "0.1"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

play.Project.playScalaSettings

ScoverageSbtPlugin.instrumentSettings

