Spark3
------
>This project tests Spark 3 features.

Warning
-------
>Anecdotal evidence suggests that Spark 3, using JDK 11, is slower than using JDK 8.
>You can target different JDK version via .sbtopts

Test
----
1. sbt clean test

Bloop
-----
1. sbt bloopInstall
2. bloop projects
3. bloop clean spark3
4. bloop compile spark3
5. bloop test spark3

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
2. Add this line ( to line 1 ): -java-home /Users/objektwerks/.sdkman/candidates/java/8.0.265.hs-adpt