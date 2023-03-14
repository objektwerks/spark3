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
1. target/test.log
2. target/app.log

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
6. shuffling (disk/network io)
7. splittable files
8. number of files and size
9. data locality
10. jvm gc
11. spark web/history ui
12. tungsten

JDK
---
>Spark 3 requires JDK 8 or greater. This project works on JDK 19. Via Sbt, use as follows:

* sbt clean test -java-home $JAVA_HOME
* sbt run -java-home $JAVA_HOME

.sbtopts
--------
1. Create an .sbtopts file in the project root directory.
2. Add this line: -java-home $JAVA_HOME
