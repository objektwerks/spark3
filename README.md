Spark3
------
>The purpose of the project is to test Spark 3 features.

Warning
-------
>While Spark 3 supports JDK 11, it is slower than using JDK 8. Consequently this project useds JDK 8.
>It also appears that a number of features in Spark 3 are slower than in Spark 2. So always benchmark! :)

Test
----
1. sbt clean test

Bloop
-----
1. sbt bloopInstall
2. bloop projects
3. bloop clean spark
4. bloop compile spark
5. bloop test spark

Logs
----
1. ./target/test.log
2. ./target/app.log

Events
------
1. ./target/local-*

Tunning
------- 
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

JDKs
----
>If you have more than one JDK installed, such as JDK 8 and JDK 11, you need to run sbt using JDK 8.
Here's a few examples:

* sbt clean test -java-home /Library/Java/JavaVirtualMachines/jdk1.8.0_202.jdk/Contents/Home
* sbt run -java-home /Library/Java/JavaVirtualMachines/jdk1.8.0_202.jdk/Contents/Home

>Or, optionally, create an .sbtopts file.
 
.sbtopts
--------
1. Create an .sbtopts file in the project root directory.
2. Add this line ( to line 1 ): -java-home /Library/Java/JavaVirtualMachines/jdk1.8.0_202.jdk/Contents/Home