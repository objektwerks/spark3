Spark 3
-------
>Tests Spark 3 features.

Architecture
------------
>This model excludes the cluster manager, such as Standalone, Yarn, Mesos and Kubernetes.
* Job 1 --> * Stage 1 --> * Partition | Task
* Driver 1 <--> * Executor
* Node 1 --> * JVM 1 --> 1 Executor
* Executor 1 --> * Partition | Task
* Task 1 --> 1 Partition

Test
----
1. sbt clean test

Logs
----
1. target/app.log
2. target/test.log

Events
------
1. target/local-*

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

JDK
---
>Spark 3 requires ***JDK 8/11/17***. This project works on ***JDK 19***!

>To target a specific JDK, via Sbt, use as follows:

1. sbt clean test -java-home $JAVA_HOME
2. sbt run -java-home $JAVA_HOME

.sbtopts
--------
>Alternatively, set up an .sbtopts file as follows:
1. Create an .sbtopts file in the project root directory.
2. Add this line to .sbtopts: -java-home $JAVA_HOME

.jvmopts
--------
>Spark 3 runs on ***JDK 17+***. See this project's ***.jvmopts*** for the ***how***.

>And see this discussion thread for the ***details***: https://stackoverflow.com/questions/72724816/running-unit-tests-with-spark-3-3-0-on-java-17-fails-with-illegalaccesserror-cl
