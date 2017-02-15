import com.trafficland.augmentsbt.releasemanagement.ReleaseManagementPlugin.autoImport.remoteGitRepoPatterns
import com.trafficland.augmentsbt.toVersion
import sbt.Keys._
import sbt._

val name = "elblogparser"
val libVersion = "0.99.0-SNAPSHOT".toReleaseFormat

lazy val parserProject =  Project(name, file("."))
  .enablePlugins(StandardPluginSet)
  .settings(
    organization := "com.trafficland",
    organizationName := "Trafficland, Inc.",
    description := "A set of opinionated SBT plugins for common build and release tasks.",
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    crossScalaVersions := Seq("2.11.8", "2.12.1"),
    scalaVersion := "2.12.1",
    version := libVersion,
    isApp := false,
    cancelable in Global := true,
    libraryDependencies ++= dependencies,
    bintrayRepository := "oss",
    bintrayOrganization := Some("trafficland"),
    remoteGitRepoPatterns ++= Seq(
      """^git@github.com:ereichert/.*\.git""".r,
      """^https://github.com/ereichert/.*\.git""".r
    )
  )

lazy val dependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)