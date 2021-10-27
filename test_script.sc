import mainargs.main

import $ivy.`org.apache.spark::spark-core:2.4.5`
import $ivy.`org.apache.spark::spark-sql:2.4.5`
import org.apache.spark.sql.SparkSession

@main
def printAppName(AppName: String):Unit=
  {
    val spark = SparkSession.builder
      .master("local[*]")
      .appName(AppName)
      .getOrCreate()
    println("Spark App Name is "+spark.sparkContext.appName)
//    spark.stop()
//    spark.close()
  }

//@main
//def printAppName(Name:String):Unit=
//{
//  println(Name)
//}