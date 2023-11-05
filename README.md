Spark 3
-------
>Spark 3 apps and tests.

JDK
---
>Spark 3, version 3.5.0, requires ***JDK 8/11/17***.

>This project has worked on ***JDK 19/20/21***. Yet not always, due to JDK internal api access errors.

>To target a specific JDK use:

1. sbt clean test -java-home $JAVA_HOME
2. sbt run -java-home $JAVA_HOME

.jvmopts
--------
>See ***.jvmopts*** for the how JDK internal api access is allowed.

>See this discussion for details: [IllegalAccessError: class StorageUtils](https://stackoverflow.com/questions/72724816/running-unit-tests-with-spark-3-3-0-on-java-17-fails-with-illegalaccesserror-cl)

Testing
-------
>Randomly ```sbt clean test``` will pass all tests; and sometimes it won't due to JDK internal access errors. 

Test
----
1. sbt clean test

Run
---
1. sbt clean run
```
Multiple main classes detected. Select one to run:
 [1] objektwerks.DeltaLakeApp
 [2] objektwerks.FlightGraphApp
 [3] objektwerks.KMeansApp
 [4] objektwerks.LinearRegressionApp
 [5] objektwerks.LogEntryApp
 [6] objektwerks.RecommendationApp

Enter number:
```

Logs
----
1. target/app.log
2. target/test.log

Events
------
1. target/local-*

Architecture
------------
>This model of Spark architecture excludes the cluster manager, such as Standalone, Yarn, Mesos and Kubernetes.
* Job 1 --> * Stage 1 --> * Task
* Driver 1 <--> * Executor
* Node 1 --> * JVM 1 --> * Executor
* Executor 1 --> * Task | Partition
* Task 1 --> 1 Partition

Tuning
------
1. kyro serialization
2. partitions
3. driver and executor memory/cores
4. cache/persist/checkpointing
5. narrow vs wide transformations
6. shuffling ( disk/network io )
7. splittable files
8. number of files and size
9. data locality
10. jvm gc
11. spark web ui
12. spark web history ui
13. tungsten

Resources
---------
1. [Spark 2](https://github.com/objektwerks/spark)