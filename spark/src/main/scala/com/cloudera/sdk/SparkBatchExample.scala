package com.cloudera.sdk

import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{ FileSystem, Path }
import org.apache.spark.rdd.RDD

object SparkBatchExample {
  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      System.err.println("Usage: SparkBatchExample <path>")
      System.exit(1)
    }

    val inpath = args(0)
    val conf = new SparkConf()
      .setAppName("Spark Batch Example")
      .set("spark.io.compression.codec", "lz4")
    val sc = new SparkContext(conf)

    conf.set("spark.cleaner.ttl", "120000")

    /* READ DATA FROM HDFS, SORT BY KEY, SAVE TO HDFS */

    val file = sc.textFile(inpath)
    val arrayTuples = file.map(line => line.split(",")).map(array => (array(0), array(1)))
    val sortedrdd = arrayTuples.repartition(1).sortByKey()

    val outpath = args(1)

    sortedrdd.saveAsTextFile(outpath)

  }
}
