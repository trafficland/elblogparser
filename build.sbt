import sbt._
import sbt.Keys._
import trafficland.opensource.sbt.plugins._
import trafficland.opensource.sbt.plugins.isApp

val name = "elblogparser"
val libVersion = "0.0.1-SNAPSHOT".toReleaseFormat()

lazy val parserProject =  Project(name, file("."))
  .enablePlugins(StandardPluginSet)
  .settings(
    scalaVersion := "2.12.1",
    version := libVersion,
    isApp := false,
    cancelable in Global := true,
    libraryDependencies ++= dependencies
  )

lazy val dependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)