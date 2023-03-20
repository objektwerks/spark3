package objektwerks

import org.apache.spark.sql.Dataset
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import SparkInstance._
import sparkSession.implicits._

class ExplainTest extends AnyFunSuite with Matchers {
  test("explain") {
    val persons = sparkSession.read.json("./data/person/person.json").as[Person].cache
    val fred = persons.map(_.name.toUpperCase).filter(_ == "FRED").cache
    fred.count shouldBe 1
    fred.head shouldBe "FRED"
    fred.explain(extended = true)
  }
}