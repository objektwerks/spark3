package objektwerks

import org.apache.spark.sql.{Dataset, Row}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import SparkInstance._
import sparkSession.implicits._

class WordCountTest extends AnyFunSuite with Matchers {
  test("dataset") {
    val lines: Dataset[String] = sparkSession.read.textFile("./data/words/gettysburg.address.txt")
    val counts = lines
      .flatMap(line => line.split("\\W+"))
      .filter(_.nonEmpty)
      .groupByKey(_.toLowerCase)
      .count
      .collect
      .map { case (line, count) => Count(line, count) }
    counts.length shouldBe 138
  }

  test("dataframe") {
    val lines: Dataset[Row] = sparkSession.read.textFile("./data/words/gettysburg.address.txt").toDF("line")
    val counts = lines
      .flatMap(row => row.getString(0).split("\\W+"))
      .filter(_.nonEmpty)
      .groupByKey(_.toLowerCase)
      .count
      .collect
    counts.length shouldBe 138
  }

  test("structured streaming") {
    sparkSession
      .readStream
      .text("./data/words")
      .flatMap(row => row.getString(0).split("\\W+"))
      .filter(_.nonEmpty)
      .groupByKey(_.toLowerCase)
      .count
      .writeStream
      .format("memory")
      .queryName("words")
      .outputMode("complete")
      .start()
      .awaitTermination(6000L) // Warning: Spark 3 is slower than Spark 2. So this await value might need to be increased.
    val words = sparkSession.sql("select * from words")
    words.count shouldBe 138
  }

  test("rdd") {
    val lines = sparkContext.textFile("./data/words/gettysburg.address.txt")
    val counts = lines.flatMap(line => line.split("\\W+"))
      .filter(_.nonEmpty)
      .map(_.toLowerCase)
      .map(word => (word, 1))
      .reduceByKey(_ + _)
      .collect
    counts.length shouldBe 138
  }
}