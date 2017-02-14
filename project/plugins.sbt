resolvers ++= Seq(
  Resolver.url("bintray-trafficland-sbt-plugins", url("https://dl.bintray.com/trafficland/sbt-plugins/"))(
    Patterns(isMavenCompatible = false, Resolver.localBasePattern)
  )
)

addSbtPlugin("com.trafficland" % "augmentsbt" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")