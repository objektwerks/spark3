package objektwerks

import java.io.File

import org.apache.spark.sql.SparkSession
import scala.util.Try

object SparkInstance {
  System.setSecurityManager(null) // Resolves Derby DB access issue.
  val sparkWarehouseDir = new File("./target/spark-warehouse").getAbsolutePath
  val sparkEventLogDir = "/tmp/spark-events"
  val sparkEventDirCreated = createSparkEventsDir(sparkEventLogDir)
  println(s"*** $sparkEventLogDir exists or was created: $sparkEventDirCreated")

  val sparkSession = SparkSession
    .builder
    .master("local[*]")
    .appName("spark-app")
    .config("spark.sql.shuffle.partitions", "4")
    .config("spark.sql.warehouse.dir", sparkWarehouseDir)
    .config("spark.eventLog.enabled", value = true)
    .config("spark.eventLog.dir", sparkEventLogDir)
    .enableHiveSupport
    .getOrCreate()
  val sparkContext = sparkSession.sparkContext
  println("*** Initialized Spark instance.")

  sys.addShutdownHook {
    sparkSession.stop()
    println("*** Terminated Spark instance.")
  }

  def createSparkEventsDir(dir: String): Boolean = {
    import java.nio.file.{Files, Paths}
    val path = Paths.get(dir)
    if (!Files.exists(path))
      Try ( Files.createDirectories(path) ).isSuccess
    else true
  }
}