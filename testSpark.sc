import mainargs.main
import $ivy.`org.apache.spark::spark-core:2.4.5`
import $ivy.`org.apache.spark::spark-sql:2.4.5`
//import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
//
//@main
//def printAppName(AppName: String):Unit=
//{
//  val spark1 = SparkSession.builder
//    .master("local[*]")
//    .appName(AppName)
//    .getOrCreate()
//  println("Spark App Name is "+spark1.sparkContext.appName)
//  spark1.stop()
//  spark1.close()
//}
@main
def printAppName(AppName: String):Unit= {
  val sparkSession: SparkSession = SparkSession.builder.master("local[1]").appName(AppName).getOrCreate()
  //////val csvPO = sparkSession.read.option("inferSchema", true).option("header", true).
  //////  csv("all_india_PO_list.csv")
  //////csvPO.createOrReplaceTempView("tabPO")
  //////val count = sparkSession.sql("select * from tabPO").count()
  //////println(count)
  //  println("hi")
  println(sparkSession.sparkContext.appName)
}
  //  val conf: SparkConf = new SparkConf()
  //  conf.setAppName(AppName)
  //  conf.set("spark.sql.caseSensitive", "true")
  //  conf.setMaster("local[2]")
  //  val ss: SparkSession = SparkSession.builder.config(conf).getOrCreate
  //  println(ss.sparkContext.appName+ "App3")

//  @main
//  def add(Name: String): Unit = {
//    println(Name + " in different script file")
//  }
