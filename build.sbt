port in container.Configuration := 9000

org.scalastyle.sbt.ScalastylePlugin.Settings

assemblyJarName in assembly := "simple-todo.jar"

mainClass in assembly := Some("JettyLauncher")
