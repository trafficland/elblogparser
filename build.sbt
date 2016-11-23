import sbt._
import sbt.Keys._
import trafficland.opensource.sbt.plugins._

val name = "elblogparser"
val libVersion = "0.0.1".toReleaseFormat()

lazy val parserProject =  Project(name, file("."))
  .enablePlugins(StandardPluginSet)
  .settings(
    scalaVersion := "2.12.1",
    version := libVersion,
    cancelable in Global := true,
    scalacOptions += "-language:implicitConversions",
      libraryDependencies ++= dependencies
  )

lazy val dependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)