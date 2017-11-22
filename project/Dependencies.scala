import sbt._

object Dependencies {
  lazy val logz            = "com.typesafe.scala-logging" %% "scala-logging"    % "3.5.0"
  lazy val logback         = "ch.qos.logback"              % "logback-classic"  % "1.1.7"
  lazy val scalaTest       = "org.scalatest"    %% "scalatest"                  % "3.0.3"
  lazy val typesafeConfig  = "com.typesafe"      % "config"                     % "1.3.2"
  lazy val sparkCore       = "org.apache.spark" %% "spark-core"                 % "2.2.0" % "provided"
  lazy val sparkMesos      = "org.apache.spark" %% "spark-mesos"                % "2.2.0"
  lazy val sparkStreaming  = "org.apache.spark" %% "spark-streaming"            % "2.2.0" % "provided"
  lazy val sparkTwitter    = "org.apache.bahir" %% "spark-streaming-twitter"    % "2.2.0"
  lazy val sparkKafka      = "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.2.0"
  lazy val confluent       = "io.confluent"      % "kafka-avro-serializer"      % "3.3.0"
  lazy val joda            = "joda-time"         % "joda-time"                  % "2.3"
}
