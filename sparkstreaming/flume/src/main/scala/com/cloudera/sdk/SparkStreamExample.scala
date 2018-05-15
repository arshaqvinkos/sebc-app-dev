package com.cloudera.sdk

import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.flume.FlumeUtils

object SparkStreamExample {
  lazy val logger = Logger.getLogger(this.getClass.getName)

  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      System.err.println("Usage: SparkStreamExample <host> <port>")
      System.exit(1)
    }

    val host = args(0)
    val port = args(1).toInt

    val conf = new SparkConf()
      .setAppName("Spark Stream Example")
      //      .setMaster("local[2]")
      .set("spark.io.compression.codec", "lz4")

    val sc = new SparkContext(conf)

    conf.set("spark.cleaner.ttl", "120000")
    conf.set("spark.driver.allowMultipleContexts", "true")

    val spark = SparkSession.builder().config(conf).getOrCreate()

    Logger.getRootLogger.setLevel(Level.WARN)

    val ssc = new StreamingContext(sc, Seconds(30))

    val flumeStream = FlumeUtils.createStream(ssc, host, port)
    val mappedlines = flumeStream.map { record => { (new String(record.event.getBody().array())) } }
    val counts = mappedlines.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey(_ + _)
    counts.print()

    /* DON'T FORGET TO START THE STREAM SESSION */
    ssc.start()
    ssc.awaitTermination()
  }
}
