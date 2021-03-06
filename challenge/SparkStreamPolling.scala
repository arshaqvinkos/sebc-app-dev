package com.cloudera.sdk

import org.apache.spark.streaming.{StreamingContext, Seconds}
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.log4j.{Level, Logger}
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.io.Text
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.HTable

object SparkStreamPolling {
    lazy val logger = Logger.getLogger(this.getClass.getName)

    def main(args: Array[String]): Unit = {
       
        val host = "localhost"
        val port = 41415

        val conf = new SparkConf()
            .setAppName("Spark Stream Example")
//          .setMaster("local[2]")
            .set("spark.io.compression.codec", "lz4")

        val sc = new SparkContext(conf)

        conf.set("spark.cleaner.ttl", "120000")
        conf.set("spark.driver.allowMultipleContexts", "true")

        val spark = SparkSession
            .builder()
            .config(conf)
            .getOrCreate()

        /* Hbase connection */
        val tableName = "Arshaq_flume"
        val hbaseMaster = "35.171.159.169"
        val hdfsPort = 60010
        val columnFamilyName = "words"
        val columnNameWord = "word"
        val columnNameCount = "count"
        

        Logger.getRootLogger.setLevel(Level.WARN)

        val ssc = new StreamingContext(sc, Seconds(30))


 val filters = List("a", "is", "the", "this")

        /* READ STREAM FROM FLUME AND PRINT COUNTS */
         val flumeStream = FlumeUtils.createStream(ssc, host, port)
        val lines = flumeStream.map {record => {(new String(record.event.getBody().array()))}}
        val counts =  lines.
                                    flatMap(_.split("\\s")).
                                    filter(w => (w !="a") && (w != "is") && (w != "the") && (w != "this")).
                                    map((_, 1)).
                                    reduceByKey(_ + _)
        
        /* HBase interaction */
    val r = scala.util.Random
    counts.foreachRDD {
      rdd =>
        /** Save word count result in hbase **/
        

        rdd.foreachPartition { iter =>
          
                  /* Hbase connection per worker */
              val hbaseConf = HBaseConfiguration.create()
             // hbaseConf.set(TableOutputFormat.OUTPUT_TABLE, tableName)
             hbaseConf.set(TableInputFormat.INPUT_TABLE, tableName)
             val hTable = new HTable(hbaseConf, tableName)
          
          iter.foreach {
          case (word, count) =>
             
            val key = word + "_" + count + "_" + r.nextInt(100)
            println(">>> key:" + key)
            val putdata = new Put(key.getBytes)
            putdata.addColumn(columnFamilyName.getBytes, columnNameWord.getBytes, word.getBytes)
            putdata.addColumn(columnFamilyName.getBytes, columnNameCount.getBytes, Bytes.toBytes(count))
            hTable.put(putdata)
            hTable.flushCommits()
        }
              }
    }

        ssc.start()
        ssc.awaitTermination()
    }
}


