package objektwerks

import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable

import SparkInstance._

class StreamingTest extends AnyFunSuite with Matchers {
  test("batch") {
    val streamingContext = new StreamingContext(sparkContext, batchDuration = Milliseconds(100))
    val dstream = textToDStream("./data/txt/license.txt", streamingContext)
    
    val wordCountDstream = countWords(dstream)
    val buffer = mutable.ArrayBuffer[(String, Int)]()
    wordCountDstream foreachRDD { rdd => buffer ++= rdd.collect }

    streamingContext.start
    streamingContext.awaitTerminationOrTimeout(1000)
    streamingContext.stop(stopSparkContext = false, stopGracefully = true)

    buffer.size shouldBe 96
  }

  test("window") {
    val streamingContext = new StreamingContext(sparkContext, batchDuration = Milliseconds(200))
    val dstream = textToDStream("./data/txt/license.txt", streamingContext)

    val wordCountDstream = countWords(dstream, windowLengthInMillis = 200, slideIntervalInMillis = 200)
    val buffer = mutable.ArrayBuffer[(String, Int)]()
    wordCountDstream foreachRDD { rdd => buffer ++= rdd.collect }

    streamingContext.start
    streamingContext.awaitTerminationOrTimeout(1000)
    streamingContext.stop(stopSparkContext = false, stopGracefully = true)

    buffer.size shouldBe 96
  }
}