import org.apache.spark.sql.types._
import org.apache.spark.sql._
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import spark.implicits._

import org.locationtech.geomesa.spark.jts._

object GeomesaSparkUDFBench {
  def main(args: Array[String]) {
    val spark = SparkSession.builder()
                  .appName("geomesa-bench")
                  .getOrCreate()
                  .withJTS
    
    var exponent = 6    // by default
    if (args.length == 1) {
        exponent = args(0).toInt
    }
    val cycleTimes = scala.math.pow(10, exponent)

    var begin = System.nanoTime
    var end = System.nanoTime

    var pointData = Seq()
    for (var i <- 1 to cycleTimes) {
        pointData :+ (i + 0.1, i + 0.1)
    }
    val pointDf = pointData.toDF("x", "y").cache()
    pointDf.createOrReplaceTempView("pointDf")
    begin = System.nanoTime
    val point = spark.sql("select st_point(x, y) from pointDf")
    point.createOrReplaceTempView("point")
    spark.sql("CACHE table point)
    end = System.nanoTime
    println("geomesa_st_point_time:" + (end - begin) / 1e9d)

    var intersectionData = Seq()
    for (var i <- 1 to cycleTimes) {
        intersectionData :+ ("POINT(0 0)", "LINESTRING ( 0 0, 2 2 )")
    }
    val intersectionDf = intersectionData.toDF("left", "right").cache()
    intersectionDf.createOrReplaceTempView("intersectionDf")
    begin = System.nanoTime
    val intersection = spark.sql("select st_intersection(left, right) from intersectionDf")
    intersection.createOrReplaceTempView("intersection")
    spark.sql("CACHE table intersection)
    end = System.nanoTime
    println("geomesa_st_intersection_time:" + (end - begin) / 1e9d)
  }
}
