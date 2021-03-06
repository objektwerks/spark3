name := "spark3"
organization := "objektwerks"
version := "0.1-SNAPSHOT"
scalaVersion := "2.12.14"
libraryDependencies ++= {
  val sparkVersion = "3.1.2"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-streaming" % sparkVersion,
    "org.apache.spark" %% "spark-sql" % sparkVersion,
    "org.apache.spark" %% "spark-hive" % sparkVersion,
    "org.apache.spark" %% "spark-mllib" % sparkVersion,
    "org.apache.spark" %% "spark-graphx" % sparkVersion,
    "org.scalikejdbc" %% "scalikejdbc" % "3.5.0",
    "com.h2database" % "h2" % "1.4.200",
    "org.slf4j" % "slf4j-api" % "1.7.30",
    "org.scalatest" %% "scalatest" % "3.2.9" % Test
  )
}
