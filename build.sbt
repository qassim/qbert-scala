name := "qbert"

version := "0.1"

scalaVersion := "2.12.4"

mainClass := Some("Bot")


libraryDependencies ++= Seq(
  "com.github.gilbertw1" %% "slack-scala-client" % "0.2.2",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "org.json4s" %% "json4s-native" % "3.6.0-M2",
  "com.typesafe" % "config" % "1.3.1",
  "org.clapper" %% "classutil" % "1.1.2"
)

