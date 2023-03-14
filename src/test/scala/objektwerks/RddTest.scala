package objektwerks

import java.nio.charset.CodingErrorAction

import org.apache.spark.HashPartitioner
import org.apache.spark.rdd.RDD
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.collection.{SortedSet, mutable}
import scala.io.{Codec, Source}

import SparkInstance._

class RddTest extends AnyFunSuite with Matchers {
  test("transformations > action") {
    val rdd = sparkContext.makeRDD(Array(1, 2, 3)).cache
    assert(rdd.filter(_ % 2 == 0).first == 2)
    assert(rdd.filter(_ % 2 != 0).first == 1)
    assert(rdd.map(_ + 1).sum == 9)
    assert(rdd.map(_ + 1).collect sameElements Array(2, 3, 4))
    assert(rdd.count == 3)
    assert(rdd.first == 1)
    assert(rdd.min == 1)
    assert(rdd.max == 3)
    assert(rdd.mean == 2.0)
    assert(rdd.variance == 0.6666666666666666)
    assert(rdd.sampleVariance == 1.0)
    assert(rdd.stdev == 0.816496580927726)
    assert(rdd.sampleStdev == 1.0)
    assert(rdd.sum == 6)
    assert(rdd.fold(0)(_ + _) == 6)
    assert(rdd.reduce(_ + _) == 6)
    assert(rdd.takeOrdered(3).toSet == SortedSet(1, 2, 3))
    assert(rdd.take(1) sameElements Array(1))
  }

  test("parallelize") {
    val xs = 1 to 1000000
    val rdd = sparkContext.parallelize(seq = xs, numSlices = 100)
    val result = rdd.filter(_ % 2 == 0).collect
    assert(result.length == 500000)
  }

  test("partitioner") {
    val rdd = sparkContext.parallelize(List((1, 1), (2, 2), (3, 3))).partitionBy(new HashPartitioner(2)).persist
    val partitioner = rdd.partitioner.get // ShuffleRDDPartition @0 / @1
    assert(partitioner.numPartitions == 2)
  }

  test("aggregate") {
    val data = 1 to 10
    val rdd = sparkContext.parallelize(data)
    val (x, y) = rdd.aggregate((0, 0))((x, y) => (x._1 + y, x._2 + 1), (x, y) => (x._1 + y._1, x._2 + y._2))
    assert(x == 55 && y == 10)
  }

  test("cogroup") {
    val leftRdd = sparkContext.makeRDD(Array((1, 2)))
    val rightRdd = sparkContext.makeRDD(Array((1, 3)))
    val cogroupRdd = leftRdd.cogroup(rightRdd)
    cogroupRdd.collect foreach { t:(Int, (Iterable[Int], Iterable[Int])) => assert( (1,(Iterable(2),Iterable(3))) == t ) }
  }

  test("join") {
    val leftRdd = sparkContext.makeRDD(Array((1, 2)))
    val rightRdd = sparkContext.makeRDD(Array((1, 3)))
    val joinRdd = leftRdd.join(rightRdd)
    joinRdd.collect foreach { t:(Int, (Int, Int)) => assert( (1, (2, 3)) == t ) }
  }

  test("sets") {
    val rdd1 = sparkContext.makeRDD(Array(1, 2, 3)).cache
    val rdd2 = sparkContext.makeRDD(Array(3, 4, 5)).cache
    assert(rdd1.union(rdd2).collect sameElements Array(1, 2, 3, 3, 4, 5))
    assert(rdd1.intersection(rdd2).collect sameElements Array(3))
    assert(rdd1.subtract(rdd2).collect.sorted sameElements Array(1, 2))
    assert(rdd2.subtract(rdd1).collect.sorted sameElements Array(4, 5))

    val rdd3 = sparkContext.makeRDD(Array(1, 1, 2, 2, 3, 3))
    assert(rdd3.distinct.collect.sorted sameElements Array(1, 2, 3))

    val rdd4 = sparkContext.makeRDD(Array(1, 2))
    val rdd5 = sparkContext.makeRDD(Array(3, 4))
    assert(rdd4.cartesian(rdd5).collect sameElements Array((1,3), (1, 4), (2, 3), (2, 4)))
  }

  test("reduceByKey") {
    val rdd = sparkContext.makeRDD(Array((1, 1), (1, 2), (1, 3))).cache
    val (key, aggregate) = rdd.reduceByKey(_ + _).first
    assert(rdd.keys.collect sameElements Array(1, 1, 1))
    assert(rdd.values.collect sameElements Array(1, 2, 3))
    assert(key == 1 && aggregate == 6)
  }

  test("groupByKey") {
    val rdd = sparkContext.makeRDD(Array((1, 1), (1, 2), (1, 3))).cache
    val (key, list) = rdd.groupByKey.first
    assert(rdd.keys.collect sameElements Array(1, 1, 1))
    assert(rdd.values.collect sameElements Array(1, 2, 3))
    assert(key == 1 && list.sum == 6)
  }

  test("sortByKey") {
    val rdd = sparkContext.makeRDD(Array((3, 1), (2, 2), (1, 3)))
    assert(rdd.reduceByKey(_ + _).sortByKey(ascending = true).collect sameElements Array((1,3), (2, 2), (3, 1)))
  }

  test("mapValues") {
    val rdd = sparkContext.makeRDD(Array((1, 1), (1, 2), (1, 3)))
    assert(12 == rdd.mapValues(_ * 2).values.sum)
  }

  test("count") {
    def countWords(rdd: RDD[String]): RDD[(String, Int)] = {
      rdd.flatMap(line => line.split("\\W+"))
        .filter(_.nonEmpty)
        .map(_.toLowerCase)
        .map(word => (word, 1))
        .reduceByKey(_ + _)
    }

    val rdd = sparkContext.textFile("./data/txt/license.txt").cache
    val totalLines = rdd.count
    assert(totalLines == 19)

    val selectedWordCount = rdd.filter(_.contains("Permission")).count
    assert(selectedWordCount == 1)

    val longestLine = rdd.map(line => line.length).reduce((a, b) => Math.max(a, b))
    assert(longestLine == 77)

    val wordCounts = countWords(rdd).cache
    assert(wordCounts.count == 96)
    val maxWordCount = wordCounts.values.max
    val (word, count) = wordCounts.filter(_._2 == maxWordCount).first
    assert(word == "the" && count == 14)
  }
}