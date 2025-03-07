package objektwerks

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import SparkInstance._
import sparkSession.implicits._

class BroadcastTest extends AnyFunSuite with Matchers {
  test("broadcast variable") {
    val keyValues = List( KeyValue(1, 1), KeyValue(2, 2), KeyValue(3, 3) )
    val broadcastKeyValues = sparkContext.broadcast( keyValues )
    broadcastKeyValues.value.length shouldBe 3
  }

  test("broadcast join") {
    val broadcastPersons = sparkContext.broadcast( sparkSession.read.json("./data/person/person.json").as[Person] )

    val persons = broadcastPersons.value
    val tasks = sparkSession.read.json("./data/task/task.json").as[Task]

    val joinCondition = persons.col("id") === tasks.col("pid")
    val personsTasks = persons.join(tasks, joinCondition)

    personsTasks.count shouldBe 4
  }
}