package objektwerks

import java.io.File

import org.apache.logging.log4j.LogManager
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.rdd.RDD

import scala.collection.mutable
import scala.util.Try

object SparkInstance {
  val logger = LogManager.getLogger(SparkInstance.getClass())
  val sparkWarehouseDir = new File("./target/spark-warehouse").getAbsolutePath
  val sparkEventLogDir = "/tmp/spark-events"
  val sparkEventDirCreated = createSparkEventsDir(sparkEventLogDir)
  logger.info(s"*** $sparkEventLogDir exists or was created: $sparkEventDirCreated")

  val sparkSession = SparkSession
    .builder()
    .master("local[*]")
    .appName("spark-app")
    .config("spark.sql.shuffle.partitions", "4")
    .config("spark.sql.warehouse.dir", sparkWarehouseDir)
    .config("spark.eventLog.enabled", value = true)
    .config("spark.eventLog.dir", sparkEventLogDir)
    .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
    .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
    .enableHiveSupport()
    .getOrCreate()
  val sparkContext = sparkSession.sparkContext
  logger.info("*** Initialized Spark instance.")

  sys.addShutdownHook {
    sparkSession.stop()
    logger.info("*** Terminated Spark instance.")
  }:Unit

  def createSparkEventsDir(dir: String): Boolean = {
    import java.nio.file.{Files, Paths}
    val path = Paths.get(dir)
    if (!Files.exists(path))
      Try ( Files.createDirectories(path) ).isSuccess
    else true
  }

  def textFileToDStream(filePath: String, sparkContext: SparkContext, streamingContext: StreamingContext): DStream[String] = {
    val queue = mutable.Queue[RDD[String]]()
    val dstream = streamingContext.queueStream(queue)
    val lines = sparkContext.textFile(filePath)
    queue += lines
    dstream
  }

  def textToDStream(filePath: String, streamingContext: StreamingContext): DStream[String] = {
    val queue = mutable.Queue[RDD[String]]()
    val dstream = streamingContext.queueStream(queue)
    val lines = SparkInstance.sparkContext.textFile(filePath)
    queue += lines
    dstream
  }

  def countWords(ds: DStream[String]): DStream[(String, Int)] = {
    ds.flatMap(line => line.split("\\W+"))
      .filter(_.nonEmpty)
      .map(_.toLowerCase)
      .map(word => (word, 1))
      .reduceByKey(_ + _)
  }

  def countWords(ds: DStream[String], windowLengthInMillis: Long, slideIntervalInMillis: Long): DStream[(String, Int)] = {
    ds.flatMap(line => line.split("\\W+"))
      .filter(_.nonEmpty)
      .map(_.toLowerCase)
      .map(word => (word, 1))
      .reduceByKeyAndWindow((x:Int, y:Int) => x + y, Milliseconds(windowLengthInMillis), Milliseconds(slideIntervalInMillis))
  }
}