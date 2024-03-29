package objektwerks

import java.util.UUID

import org.apache.spark.storage.StorageLevel
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import SparkInstance._
import sparkSession.implicits._

class PartitionTest extends AnyFunSuite with Matchers with BeforeAndAfterAll {
  val dataframe = (1 to 10).toDF("number").persist(StorageLevel.MEMORY_AND_DISK)

  override def afterAll(): Unit = dataframe.unpersist()

  test("partition") { // wide dependency-transformation, full ( evenly distributed ) shuffle
    dataframe.rdd.partitions.length shouldEqual 8
    dataframe.write.csv(s"./target/partitioned-numbers-${UUID.randomUUID.toString}")
  }

  test("coalesce") { // narrow dependency-transformation, not a full shuffle, designed to reduce partitions
    val coalesced = dataframe.coalesce(2)
    coalesced.rdd.partitions.length shouldEqual 2
    coalesced.write.csv(s"./target/coalesced-numbers-${UUID.randomUUID.toString}")
  }

  test("repartition") {
    dataframe.repartition(4).rdd.partitions.length shouldEqual 4
    dataframe.repartition(2).rdd.partitions.length shouldEqual 2
  }

  test("partitionBy") {
    val persons = sparkSession.read.json("./data/person/person.json").cache
    val file = s"./target/partitionby-roles-${UUID.randomUUID.toString}"
    persons
      .repartition(2)
      .write
      .partitionBy("role")
      .parquet(file)
  }
}