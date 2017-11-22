# twitter-spark-ingest
Spark Streaming Twitter ingest with moving average calculation examples

## Prerequisites

This Spark Streaming job uses the Twitter4J API and needs a set of
Twitter Developer credential in addition to sbt 0.13.x and Spark 2.2.x
in order to run.

## Usage

### Configure Twitter API Credentials

Create an environment.conf in the top-level directory
(where you will run the Spark Driver from)
or edit your **src/main/resources/application.conf** file.
Fill in your Twitter-provided consumer-api-key,
consumer-secret, access-token and access-token-secret.

Put your Twitter account credentials in place of
w, x, y and z.

```
twitter {
  consumer-api-key = "wwwwwwwwwwwwwwwwwwwwwwwww"
  consumer-secret = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
  access-token = "yyyyyyyyyyyyyyyyyy-yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy"
  access-token-secret = "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"
}
```

### Configure Local Spark Install

You need to run this Spark Driver application from a machine
where Spark 2.2.x is already installed.  This is true whether
you want to run the Spark Job locally or you want it run
in a distributed manner on a Mesos or Yarn Cluster.

### Run Locally

```
sbt assembly
spark-deploy target/scala-2.11/TwitterIngest-assembly-0.1.0-SNAPSHOT.jar
```

### Submit to a Mesos Cluster

```
sbt assembly
spark-submit --master mesos://master:5050 target/scala-2.11/TwitterIngest-assembly-0.1.0-SNAPSHOT.jar 2> ingest.log
```

Look at ingest.log if you don't get a Top 10 list of hashtags within a minute.
