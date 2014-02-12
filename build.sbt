name := "simple-todo"

version := "0.1"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2",
  jdbc,
  anorm,
  cache
)     

play.Project.playScalaSettings

ScoverageSbtPlugin.instrumentSettings

