name := "SparkBatchExample"

version := "1.0"

scalaVersion := "2.11.8"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

resolvers +=  "cloudera-repos" at "http://repository.cloudera.com/artifactory/cloudera-repos/"

libraryDependencies ++= Seq("org.apache.spark" %% "spark-core" % "2.3.0.cloudera2" % "provided")
