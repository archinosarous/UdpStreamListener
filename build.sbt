name := "UdpStreamListener"

version := "1.0"

scalaVersion := "2.11.7"
libraryDependencies ++= Seq(
  "com.typesafe.akka"      %% "akka-actor"          % "2.4.9-RC2",
  "com.typesafe.akka"      %% "akka-stream"         % "2.4.9-RC2",
  "com.typesafe.play"      %% "play-json"           % "2.6.0"
)