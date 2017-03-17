import com.trafficland.augmentsbt.releasemanagement.ReleaseManagementPlugin.autoImport.remoteGitRepoPatterns
import sbt.Keys._
import sbt._

val name = "elblogparser"
val libVersion = "1.0.1-SNAPSHOT".toReleaseFormat

lazy val parserProject =  Project(name, file("."))
  .enablePlugins(StandardPluginSet)
  .settings(
    organization := "com.trafficland",
    organizationName := "Trafficland, Inc.",
    description := "AWS ELB Access Log Parser.",
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    crossScalaVersions ++= Seq("2.11.8"),
    scalaVersion := "2.12.1",
    version := libVersion,
    isApp := false,
    cancelable in Global := true,
    libraryDependencies ++= dependencies,
    bintrayRepository := "oss",
    bintrayOrganization := Some("trafficland"),
    publishMavenStyle := false,
    remoteGitRepoPatterns ++= Seq(
      """^git@github.com:trafficland/.*\.git""".r,
      """^https://github.com/trafficland/.*\.git""".r
    )
  )

lazy val dependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
