package objektwerks

import org.apache.spark.sql.{Dataset, Row}
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable

import SparkInstance._
import sparkSession.implicits._

class WordCountTest extends AnyFunSuite with Matchers {
  test("dataset") {
    val lines = sparkSession.read.textFile("./data/words/gettysburg.address.txt")
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
    val lines = sparkSession.read.textFile("./data/words/gettysburg.address.txt").toDF("line")
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
      .awaitTermination(6000L)
    val words = sparkSession.sql("select * from words")
    words.count shouldBe 138
  }
}