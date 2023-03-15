package objektwerks

import java.nio.charset.CodingErrorAction

import org.apache.spark.sql.functions.window

import SparkInstance._
import sparkSession.implicits._
import LogEntry._

object LogEntryApp extends App {
  sparkSession
    .readStream
    .text("./data/log")
    .flatMap(rowToLogEntry)
    .select("status", "dateTime", "ip")
    .withWatermark("dateTime", "1 minute")
    .groupBy($"status", $"ip", window($"dateTime", "15 seconds"))
    .count
    .orderBy("window")
    .writeStream
    .outputMode("complete")
    .foreach(rowForeachWriter)
    .start
    .awaitTermination(5000)

    sys.exit()
}