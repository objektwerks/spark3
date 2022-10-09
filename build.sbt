name := "spark3"
organization := "objektwerks"
version := "0.1-SNAPSHOT"
scalaVersion := "2.13.9"
libraryDependencies ++= {
  val sparkVersion = "3.3.0"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-streaming" % sparkVersion,
    "org.apache.spark" %% "spark-sql" % sparkVersion,
    "org.apache.spark" %% "spark-hive" % sparkVersion,
    "org.apache.spark" %% "spark-mllib" % sparkVersion,
    "org.apache.spark" %% "spark-graphx" % sparkVersion,
    "org.scalikejdbc" %% "scalikejdbc" % "3.5.0", // Can't upgrade to 4.0.0
    "com.h2database" % "h2" % "2.1.214",
    "org.slf4j" % "slf4j-api" % "2.0.3",
    "org.scalatest" %% "scalatest" % "3.2.14" % Test
  )
}
