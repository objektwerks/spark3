package objektwerks

import org.apache.spark.sql.functions._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import SparkInstance._
import sparkSession.implicits._

/**
  * This test is inspired by this article:
  * https://towardsdatascience.com/spark-3-2-session-windowing-feature-for-streaming-data-e404d92e267
  */
class WindowTest extends AnyFunSuite with Matchers {
  val data = List(
    Event(1, "2023-01-02 15:30:00"),
    Event(1, "2023-01-02 15:30:30"),
    Event(1, "2023-01-02 15:31:00"),
    Event(1, "2023-01-02 15:31:50"),
    Event(1, "2023-01-02 15:31:55"),
    Event(2, "2023-01-02 15:33:00"),
    Event(2, "2023-01-02 15:35:20"),
    Event(2, "2023-01-02 15:37:00"),
    Event(3, "2023-01-02 15:30:30"),
    Event(3, "2023-01-02 15:31:00"),
    Event(3, "2023-01-02 15:31:50"),
    Event(3, "2023-01-02 15:31:55"),
    Event(3, "2023-01-02 15:33:00"),
    Event(3, "2023-01-02 15:35:20"),
    Event(3, "2023-01-02 15:37:00"),
    Event(3, "2023-01-02 15:40:00"),
    Event(3, "2023-01-02 15:45:00"),
    Event(3, "2023-01-02 15:46:00"),
    Event(3, "2023-01-02 15:47:30"),
    Event(3, "2023-01-02 15:48:00"),
    Event(3, "2023-01-02 15:48:10"),
    Event(3, "2023-01-02 15:48:20"),
    Event(3, "2023-01-02 15:48:30"),
    Event(3, "2023-01-02 15:50:00"),
    Event(3, "2023-01-02 15:53:00"),
    Event(3, "2023-01-02 15:54:30"),
    Event(3, "2023-01-02 15:55:00"),
    Event(4, "2023-01-02 15:50:30"),
    Event(4, "2023-01-02 15:52:00"),
    Event(4, "2023-01-02 15:50:30"),
    Event(4, "2023-01-02 15:52:00"),
    Event(4, "2023-01-02 15:50:30"),
    Event(4, "2023-01-02 15:52:00")
  )
  val dataset = data.toDS().cache()

  test("tumbling") {
    val dataframe = dataset
      .withWatermark("datetime", "10 minutes")
      .groupBy(
        col("id"),
        window(col("datetime"), "10 minutes")
      )
      .count()
    assert( dataframe.collect().nonEmpty )
  }

  test("sliding") {
    val dataframe = dataset
      .withWatermark("datetime", "10 minutes")
      .groupBy(
        col("id"),
        window(col("datetime"), "10 minutes", "5 minutes")
      )
      .count()
    assert( dataframe.collect().nonEmpty )
  }

  test("session") {

  }
}