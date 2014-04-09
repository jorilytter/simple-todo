name := "simple-todo"

version := "0.1"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "org.scalatra" % "scalatra_2.10" % "2.2.2",
  "org.specs2" % "specs2_2.10" % "2.3.10"
)     

ScoverageSbtPlugin.instrumentSettings

