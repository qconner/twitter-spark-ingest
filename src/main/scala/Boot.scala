
package net.qns.delphinus.twitter.ingest


// Java
import java.io.File

// Scala
//import scala.util.{Try, Success, Failure}
import collection.JavaConversions._

// Typesafe / lightbend
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging

// Spark
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

// Spark Streaming
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}

// Twitter
import org.apache.spark.streaming.twitter.TwitterUtils



object Boot extends App with StrictLogging {

  override def main(args: Array[String]) = {

    // default config in src/main/resources.application.conf
    val baseConfig = ConfigFactory.load()

    // local config settings in environment.conf
    val config = ConfigFactory.parseFile(new File("environment.conf")).withFallback(baseConfig)

    // twitter API 
    configureTwitter(config.getConfig("twitter"))

    // keyword filters
    val filters = config.getConfig("filter").getStringList("keywords").toList

    logger.info("Connecting to Twitter streaming API")

    // RRD batch period in secs
    val ssPeriod = config.getConfig("spark").getInt("period-in-secs")
    val (ssc, stream) = twitterStream(filters, ssPeriod)

    // extract text from Tweet (Twitter status update)
    val tweets: DStream[String] = stream map { x =>
      //println(x.getCreatedAt + "  " + x.getText take 168)
      x.getText
    }

    // extract hashtags from status text
    val hashtags: DStream[String] = stream flatMap { x =>
    
      // split tweet on whitespace
      val words: Array[String] = x.getText.split("\\s+")
      
      // keep words starting with #
      // and convert to lower case
      words.filter(_.startsWith("#")).map(_.toLowerCase.trim)
    }

    // tweet occurrences
    val tweetCount: DStream[Int] = tweets.map(x => 1).reduceByWindow( (x, y) => x + y, Seconds(ssPeriod), Seconds(ssPeriod))

    // hashtag occurrences
    val hashtagCount: DStream[Int] = hashtags.map(x => 1).reduceByWindow( (x: Int, y: Int) => x + y, Seconds(ssPeriod), Seconds(ssPeriod))

    // add up matching hashtag counts, within the specified time period
    val hashtagCounts = hashtags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(ssPeriod))

    // print item counts
    tweetCount foreachRDD { rdd: RDD[Int] =>
      rdd.collect.size match {
        case x if (x > 0) => print("\n%d tweets" format rdd.collect.head)
      }
    }
    hashtagCount foreachRDD { rdd: RDD[Int] =>
      rdd.collect.size match {
        case x if (x > 0) => println(" contained %d hashtags" format rdd.collect.head)
        case _ => println
      }
    }


    // print out top N
    val N = config.getConfig("hashtag").getInt("top-N")
    hashtagCounts foreachRDD { rdd: RDD[(String, Int)] =>

      println("TOP %d hashtags out of %d unique:" format (N, rdd.collect.size)) 

      rdd.sortBy(_._2, false).collect take N foreach {
        case (tag, count) => println(s"${tag}: ${count}")
      }
      println
    }

    // start the spark job
    ssc.start
    
    // run until killed
    ssc.awaitTermination
  }


  //  set System props for Twitter4J API
  def configureTwitter(conf: Config): Unit = {
    System.setProperty("twitter4j.oauth.consumerKey", conf.getString("consumer-api-key"))
    System.setProperty("twitter4j.oauth.consumerSecret", conf.getString("consumer-secret"))
    System.setProperty("twitter4j.oauth.accessToken", conf.getString("access-token"))
    System.setProperty("twitter4j.oauth.accessTokenSecret", conf.getString("access-token-secret"))
  }

  // return a Spark Streaming Context and Spark DStream of tweets
  def twitterStream(filters: List[String], period: Int): (StreamingContext, InputDStream[twitter4j.Status]) = {
    logger.debug("creating Spark context")
    val sc = new SparkConf().setAppName("Twitter Ingest").setMaster("local[4]")
    logger.debug(sc.toDebugString)

    val ssc = new StreamingContext(sc, Seconds(period))

    val stream = TwitterUtils.createStream(ssc, None, filters)
    (ssc, stream)
  }

}
