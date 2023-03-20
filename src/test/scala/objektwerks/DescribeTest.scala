package objektwerks

import org.apache.spark.sql.Dataset
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import SparkInstance._
import sparkSession.implicits._
  
class DescribeTest extends AnyFunSuite with Matchers {
  test("describe") {
    val persons = sparkSession.read.json("./data/person/person.json").as[Person].cache
    persons.count shouldBe 4
    persons.describe("id", "age", "name", "role").show()
  }
}