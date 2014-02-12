name := "simple-todo"

version := "0.1"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "org.mongodb" %% "casbah" % "2.6.5",
  "com.novus" %% "salat" % "1.9.5",
  jdbc,
  anorm,
  cache
)     

play.Project.playScalaSettings

ScoverageSbtPlugin.instrumentSettings

