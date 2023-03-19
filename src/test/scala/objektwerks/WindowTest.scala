package objektwerks

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import SparkInstance._
import sparkSession.implicits._

class WindowTest extends AnyFunSuite with Matchers {
  val data = List(
    Event(1, "2019-01-02 15:30:00"),
    Event(1, "2019-01-02 15:30:30"),
    Event(1, "2019-01-02 15:31:00"),
    Event(1, "2019-01-02 15:31:50"),
    Event(1, "2019-01-02 15:31:55"),
    Event(2, "2019-01-02 15:33:00"),
    Event(2, "2019-01-02 15:35:20"),
    Event(2, "2019-01-02 15:37:00"),
    Event(3, "2019-01-02 15:30:30"),
    Event(3, "2019-01-02 15:31:00"),
    Event(3, "2019-01-02 15:31:50"),
    Event(3, "2019-01-02 15:31:55"),
    Event(3, "2019-01-02 15:33:00"),
    Event(3, "2019-01-02 15:35:20"),
    Event(3, "2019-01-02 15:37:00"),
    Event(3, "2019-01-02 15:40:00"),
    Event(3, "2019-01-02 15:45:00"),
    Event(3, "2019-01-02 15:46:00"),
    Event(3, "2019-01-02 15:47:30"),
    Event(3, "2019-01-02 15:48:00"),
    Event(3, "2019-01-02 15:48:10"),
    Event(3, "2019-01-02 15:48:20"),
    Event(3, "2019-01-02 15:48:30"),
    Event(3, "2019-01-02 15:50:00"),
    Event(3, "2019-01-02 15:53:00"),
    Event(3, "2019-01-02 15:54:30"),
    Event(3, "2019-01-02 15:55:00"),
    Event(4, "2019-01-02 15:50:30"),
    Event(4, "2019-01-02 15:52:00"),
    Event(4, "2019-01-02 15:50:30"),
    Event(4, "2019-01-02 15:52:00"),
    Event(4, "2019-01-02 15:50:30"),
    Event(4, "2019-01-02 15:52:00")
  )
  val dataset = data.toDS()

  test("tumbling") {

  }

  test("sliding") {

  }

  test("session") {

  }
}