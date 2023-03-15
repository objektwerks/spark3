package objektwerks

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import Person._
import SparkInstance._
import sparkSession.implicits._

class StructuredStreamingTest extends AnyFunSuite with Matchers {
  test("structured streaming") {
    sparkSession
      .readStream
      .schema(personStructType)
      .json("./data/person")
      .as[Person]
      .writeStream
      .foreach(personForeachWriter)
      .start
      .awaitTermination(3000L)
  }
}