import sbt._
import sbt.Keys._
import com.trafficland.augmentsbt.toVersion

val name = "elblogparser"
val libVersion = "0.0.1-SNAPSHOT".toReleaseFormat

lazy val parserProject =  Project(name, file("."))
  .enablePlugins(StandardPluginSet)
  .settings(
    organization := "com.trafficland",
    crossScalaVersions := Seq("2.11.8", "2.12.1"),
    scalaVersion := "2.12.1",
    version := libVersion,
    isApp := false,
    cancelable in Global := true,
    libraryDependencies ++= dependencies
  )

lazy val dependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)