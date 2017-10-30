import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "net.qns.delphinus",
      scalaVersion := "2.11.11",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Ingest Twitter",
    libraryDependencies += scalaTest % Test,  // scala test
    libraryDependencies += logz,              // scala logging
    libraryDependencies += logback,           // logback
    libraryDependencies += typesafeConfig,    // typesafe (lightbend) config
//    libraryDependencies += sparkMesos,
    libraryDependencies += sparkCore,
    libraryDependencies += sparkStreaming,
    libraryDependencies += sparkTwitter,
//    libraryDependencies += sparkKafka,
//    libraryDependencies += confluent,
    libraryDependencies += joda
  )
