name := "spark3"
organization := "objektwerks"
version := "0.1-SNAPSHOT"
scalaVersion := "2.13.16"
libraryDependencies ++= {
  val sparkVersion = "3.5.6"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-sql" % sparkVersion,
    "org.apache.spark" %% "spark-streaming" % sparkVersion,
    "org.apache.spark" %% "spark-hive" % sparkVersion,
    "org.apache.spark" %% "spark-mllib" % sparkVersion,
    "org.apache.spark" %% "spark-graphx" % sparkVersion,
    "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0",
    "io.delta" %% "delta-core" % "2.4.0",
    "org.scalikejdbc" %% "scalikejdbc" % "4.3.2",
    "com.h2database" % "h2" % "2.4.240",
    "org.scalatest" %% "scalatest" % "3.2.19" % Test
  )
}
