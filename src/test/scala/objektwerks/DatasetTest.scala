package objektwerks

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, Row}
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.functions._
import org.apache.spark.storage.StorageLevel
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import SparkInstance._
import sparkSession.implicits._

class DatasetTest extends AnyFunSuite with Matchers with BeforeAndAfterAll {
  val dataset = sparkSession.read.json("./data/person/person.json").as[Person].persist(StorageLevel.MEMORY_ONLY)
  dataset.write.json("./target/dataset/person.json")

  override def afterAll(): Unit = dataset.unpersist()

  test("dataset") {
    dataset.count shouldBe 4
    assert(dataset.toDF.isInstanceOf[Dataset[Row]])
    assert(dataset.rdd.isInstanceOf[RDD[Person]])
  }

  test("column") {
    val idColumn = dataset.col("id")
    val nameColumn = col("name")
    val ageColumn = column("age")
    val roleColumn = expr("role")
    dataset
      .select(idColumn, nameColumn, ageColumn, roleColumn)
      .as[Person]
      .count shouldBe 4
  }

  test("selectExpr") {
    dataset
      .selectExpr("id", "name", "age", "role")
      .as[Person]
      .count shouldBe 4
  }

  test("add column") {
    dataset
      .withColumn("dogAge", $"age" * 7)
      .as[PersonAsDog]
      .head
      .dogAge shouldBe 168
  }

  test("update") {
    val incrementAgeNameToUpper = dataset
      .withColumn("age", 'age + 1)
      .withColumn("name", upper('name))
      .as[Person]
      .cache
    incrementAgeNameToUpper.count shouldBe 4
    incrementAgeNameToUpper.head.age shouldBe 25
    incrementAgeNameToUpper.head.name shouldBe "FRED"
  }

  test("transform") {
    def incrementAge(ds: Dataset[Person]): Dataset[Person] = ds.withColumn("age", $"age" + 1).as[Person]
    def nameToUpper(ds: Dataset[Person]): Dataset[Person] = ds.withColumn("name", upper($"name")).as[Person]
    val incrementAgeNameToUpper = dataset
      .transform(incrementAge)
      .transform(nameToUpper)
      .cache
    incrementAgeNameToUpper.count shouldBe 4
    incrementAgeNameToUpper.head.age shouldBe 25
    incrementAgeNameToUpper.head.name shouldBe "FRED"
  }

  test("map") {
    val mapNameToUpperCase = dataset.map(_.name.toUpperCase).cache
    mapNameToUpperCase.count shouldBe 4
    mapNameToUpperCase.head shouldBe "FRED"
  }

  test("filter") {
    val filterByName = dataset.filter(_.name == "barney").cache
    filterByName.count shouldBe 1
    filterByName.head.name shouldBe "barney"

    val filterByAge = dataset.filter(_.age > 23).cache
    filterByAge.count shouldBe 1
    filterByAge.head.age shouldBe 24
  }

  test("filter > map") {
    val betty = dataset
      .filter(_.name == "betty")
      .map(_.name.toUpperCase)
      .cache
    betty.count shouldBe 1
    betty.head shouldBe "BETTY"
  }

  test("sort") {
    val sortByName = dataset.sort('name).cache
    sortByName.count shouldBe 4
    sortByName.head.name shouldBe "barney"
  }

  test("select > orderBy") {
    val orderByName = dataset
      .select('name)
      .orderBy('name)
      .as[String]
      .cache
    orderByName.count shouldBe 4
    orderByName.head shouldBe "barney"
  }

  test("select > agg > case class") {
    dataset.select(min(col("age"))).map(row => Age(row.getLong(0))).head shouldBe Age(21)
    dataset.select(max(col("age"))).map(row => Age(row.getLong(0))).head shouldBe Age(24)
  }

  test("groupBy > avg") {
    val groupByRole = dataset
      .groupBy('role)
      .avg("age")
      .as[(String, Double)]
      .cache
    groupByRole.count shouldBe 2
    groupByRole.collect.foreach {
      case ("husband", avgAge) => avgAge shouldBe 23.0
      case ("wife", avgAge) => avgAge shouldBe 22.0
      case _ => fail("groupBy > avg test failed!")
    }
  }

  test("groupBy > agg(min, avg, max)") {
    val groupByRole = dataset
      .groupBy("role")
      .agg(
        min("age"),
        avg("age"),
        max("age")
      )
      .cache
    groupByRole.count shouldBe 2
    groupByRole.collect.foreach {
      case Row("husband", minAge, avgAge, maxAge) =>
        minAge shouldBe 22
        avgAge shouldBe 23.0
        maxAge shouldBe 24
      case Row("wife", minAge, avgAge, maxAge) =>
        minAge shouldBe 21
        avgAge shouldBe 22.0
        maxAge shouldBe 23
      case _ => fail("groupBy > agg( min, avg, max) test failed!")
    }
  }

  test("when > otherwise") {
    val personsWithGender = dataset
      .withColumn("gender", when($"role" === "husband", "male")
      .otherwise("female"))
      .as[PersonWithGender]
    personsWithGender.collect.foreach {
      case PersonWithGender(_, _, _, "husband", gender) => gender shouldBe "male"
      case PersonWithGender(_, _, _, "wife", gender) => gender shouldBe "female"
      case _ => fail("when > otherwise test failed!")
    }
  }

  test("window") {
    val window = Window.partitionBy('role).orderBy($"age".desc)
    val ranking = rank.over(window).as("rank")
    val result = dataset
      .select(col("role"), col("name"), col("age"), ranking)
      .as[(String, String, Long, Int)]
      .cache
    ("wife", "wilma", 23, 1) shouldEqual result.head
  }

  test("join") {
    val persons = sparkSession.read.json("./data/person/person.json").as[Person].cache
    val tasks = sparkSession.read.json("./data/task/task.json").as[Task].cache
    persons.count shouldBe 4
    tasks.count shouldBe 4

    val joinCondition = persons.col("id") === tasks.col("pid")
    val personsTasks = persons.joinWith(tasks, joinCondition)
    personsTasks.count shouldBe 4
  }
}