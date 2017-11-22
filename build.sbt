import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "net.qns.delphinus",
      scalaVersion := "2.11.11",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "TwitterIngest",
    libraryDependencies += scalaTest % Test,  // scala test
    libraryDependencies += logz,              // scala logging
    libraryDependencies += logback,           // logback
    libraryDependencies += typesafeConfig,    // typesafe (lightbend) config
    libraryDependencies += sparkCore,
    libraryDependencies += sparkStreaming,
    libraryDependencies += sparkTwitter,
    libraryDependencies += joda
  )

assemblyMergeStrategy in assembly := {
    case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
    case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
    case PathList("org", "apache", xs @ _*) => MergeStrategy.last
    case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}

