import org.apache.spark.sql.types._
import org.apache.spark.sql._
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

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
    val cycleTimes = scala.math.pow(10, exponent).toInt
    println("cycleTimes: " + cycleTimes)

    var begin = System.nanoTime
    var end = System.nanoTime
    var index = 0

    import spark.implicits._
    var pointData: Seq[(Double, Double)] = Nil
    for ( index <- 1 to cycleTimes) {
        pointData :+ (index + 0.1, index + 0.1)
    }
    println(pointData)
    val pointDf = pointData.toDF("x", "y").cache()
    pointDf.createOrReplaceTempView("pointDf")
    begin = System.nanoTime
    val point = spark.sql("select st_point(x, y) from pointDf")
    point.createOrReplaceTempView("point")
    spark.sql("CACHE table point")
    end = System.nanoTime
    println("geomesa_st_point_time: " + (end - begin) / 1e9d)

    // TODO: no this function
    /*
    var intersectionData: Seq[(String, String)] = Nil
    for ( index <- 1 to cycleTimes) {
        intersectionData :+ ("POINT(0 0)", "LINESTRING ( 0 0, 2 2 )")
    }
    val intersectionWkt = intersectionData.toDF("leftWkt", "rightWkt").cache()
    intersectionWkt.createOrReplaceTempView("intersectionWkt")
    val intersectionDf = spark.sql("select st_geomFromWkt(leftWkt) as left, st_geomFromWkt(rightWkt) as right from intersectionWkt").cache()
    intersectionDf.createOrReplaceTempView("intersectionDf")
    begin = System.nanoTime
    val intersection = spark.sql("select st_intersection(left, right) from intersectionDf")
    intersection.createOrReplaceTempView("intersection")
    spark.sql("CACHE table intersection")
    end = System.nanoTime
    println("geomesa_st_intersection_time: " + (end - begin) / 1e9d)
    */
  }
}
