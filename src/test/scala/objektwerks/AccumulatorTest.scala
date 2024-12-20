package objektwerks

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.collection.JavaConverters._

import SparkInstance._

class AccumulatorTest extends AnyFunSuite with Matchers {
  test("long accumulator") {
    val longAcc = sparkContext.longAccumulator("longAcc")
    longAcc.add(1)
    longAcc.name.get shouldBe "longAcc"
    longAcc.value shouldBe 1
  }

  test("double accumulator") {
    val doubleAcc = sparkContext.doubleAccumulator("doubleAcc")
    doubleAcc.add(1.0)
    doubleAcc.name.get shouldBe "doubleAcc"
    doubleAcc.value shouldBe 1.0
  }

  test("collection accumulator") {
    val intsAcc = sparkContext.collectionAccumulator[Int]("intsAcc")
    intsAcc.add(1)
    intsAcc.add(2)
    intsAcc.add(3)
    intsAcc.name.get shouldBe "intsAcc"
    intsAcc.value.asScala.sum shouldEqual 6
  }
}