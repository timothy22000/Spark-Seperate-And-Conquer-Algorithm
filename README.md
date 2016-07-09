# Spark-Seperate-And-Conquer-Algorithm

This repository contains code for my MSc work with regards to implementing a Separate and Conquer Algorithm in Spark 
as well as its evaluation metrics (Accuracy, Precision, Recall) that was used to analyze large datasets using distributed in-memory
data processing capabilities in Spark across a number of commodity machines (1, 5, 10, 15, 20) in a cluster.

# Setting up Spark

For Mac Users, you can use ```brew install apache-spark``` to easily install Spark.

Alternatively, you can download it from:
* [Spark] (http://spark.apache.org/downloads.html)

Quick start on Spark and to test your Spark installation:
http://spark.apache.org/docs/latest/quick-start.html

The Maven Dependency required in the project:
```
  <dependency>
      <groupId>org.apache.spark</groupId>
      <artifactId>spark-core_2.10</artifactId>
      <version>1.6.2</version>
  </dependency>
```

Additional Notes:
- You can set up your IDE (IntelliJ or Eclipse) to run a Spark application locally inside the IDE without packaging a uber jar:
	* https://cwiki.apache.org/confluence/display/SPARK/Useful+Developer+Tools#UsefulDeveloperTools-IDESetup

[Wiki Page With Project Progress] (http://timothy22000.wikidot.com/main)
