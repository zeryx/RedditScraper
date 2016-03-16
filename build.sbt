name := "redditScraper"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

libraryDependencies ++= Seq(
  "com.algorithmia" % "algorithmia-client" % "1.0.6",
  "org.scalatest" %% "scalatest" % "3.0.0-M15",
  "javax.validation" % "validation-api" % "1.1.0.Final",
  "com.typesafe.play" %% "play-json" % "2.5.0"
)

resolvers += Resolver.mavenLocal
