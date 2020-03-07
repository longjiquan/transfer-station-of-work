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

    // Seq.toDF
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

    var isValidData: Seq[(String)] = Nil
    for ( index <- 1 to cycleTimes) {
        isValidData :+ "POINT (30 10)"
    }
    val isValidWkt = isValidData.toDF("wkt").cache()
    isValidWkt.createOrReplaceTempView("isValidWkt")
    val isValidDf = spark.sql("select st_geomFromWkt(wkt) as geos from isValidWkt").cache()
    isValidDf.createOrReplaceTempView("isValidDf")
    begin = System.nanoTime
    val isValid = spark.sql("select st_isValid(geos) from isValidDf")
    isValid.createOrReplaceTempView("isValid")
    spark.sql("CACHE table isValid")
    end = System.nanoTime
    println("geomesa_st_isValid_time: " + (end - begin) / 1e9d)

    var equalsData: Seq[(String, String)] = Nil
    for ( index <- 1 to cycleTimes) {
        equalsData :+ ("LINESTRING(0 0, 10 10)", "LINESTRING(0 0, 5 5, 10 10)")
    }
    val equalsWkt = equalsData.toDF("leftWkt", "rightWkt").cache()
    equalsWkt.createOrReplaceTempView("equalsWkt")
    val equalsDf = spark.sql("select st_geomFromWkt(leftWkt) as left, st_geomFromWkt(rightWkt) as right from equalsWkt").cache()
    equalsDf.createOrReplaceTempView("equalsDf")
    begin = System.nanoTime
    val equals = spark.sql("select st_equals(left, right) from equalsDf")
    equals.createOrReplaceTempView("equals")
    spark.sql("CACHE table equals")
    end = System.nanoTime
    println("geomesa_st_equals_time: " + (end - begin) / 1e9d)

    var touchesData: Seq[(String, String)] = Nil
    for ( index <- 1 to cycleTimes) {
        touchesData :+ ("LINESTRING(0 0, 1 1, 0 2)", "POINT(0 2)")
    }
    val touchesWkt = touchesData.toDF("leftWkt", "rightWkt").cache()
    touchesWkt.createOrReplaceTempView("touchesWkt")
    val touchesDf = spark.sql("select st_geomFromWkt(leftWkt) as left, st_geomFromWkt(rightWkt) as right from touchesWkt").cache()
    touchesDf.createOrReplaceTempView("touchesDf")
    begin = System.nanoTime
    val touches = spark.sql("select st_touches(left, right) from touchesDf")
    touches.createOrReplaceTempView("touches")
    spark.sql("CACHE table touches")
    end = System.nanoTime
    println("geomesa_st_touches_time: " + (end - begin) / 1e9d)

    var overlapsData: Seq[(String, String)] = Nil
    for ( index <- 1 to cycleTimes) {
        overlapsData :+ ("POLYGON((1 1, 4 1, 4 5, 1 5, 1 1))", "POLYGON((3 2, 6 2, 6 6, 3 6, 3 2))")
    }
    val overlapsWkt = overlapsData.toDF("leftWkt", "rightWkt").cache()
    overlapsWkt.createOrReplaceTempView("overlapsWkt")
    val overlapsDf = spark.sql("select st_geomFromWkt(leftWkt) as left, st_geomFromWkt(rightWkt) as right from overlapsWkt").cache()
    overlapsDf.createOrReplaceTempView("overlapsDf")
    begin = System.nanoTime
    val overlaps = spark.sql("select st_overlaps(left, right) from overlapsDf")
    overlaps.createOrReplaceTempView("overlaps")
    spark.sql("CACHE table overlaps")
    end = System.nanoTime
    println("geomesa_st_overlaps_time: " + (end - begin) / 1e9d)

    var crossesData: Seq[(String, String)] = Nil
    for ( index <- 1 to cycleTimes) {
        crossesData :+ ("MULTIPOINT((1 3), (4 1), (4 3))", "POLYGON((2 2, 5 2, 5 5, 2 5, 2 2))"))
    }
    val crossesWkt = crossesData.toDF("leftWkt", "rightWkt").cache()
    crossesWkt.createOrReplaceTempView("crossesWkt")
    val crossesDf = spark.sql("select st_geomFromWkt(leftWkt) as left, st_geomFromWkt(rightWkt) as right from crossesWkt").cache()
    crossesDf.createOrReplaceTempView("crossesDf")
    begin = System.nanoTime
    val crosses = spark.sql("select st_crosses(left, right) from crossesDf")
    crosses.createOrReplaceTempView("crosses")
    spark.sql("CACHE table crosses")
    end = System.nanoTime
    println("geomesa_st_crosses_time: " + (end - begin) / 1e9d)

    var isSimpleData: Seq[(String)] = Nil
    for ( index <- 1 to cycleTimes) {
        isSimpleData :+ "POLYGON((1 2, 3 4, 5 6, 1 2))"
    }
    val isSimpleWkt = isSimpleData.toDF("wkt").cache()
    isSimpleWkt.createOrReplaceTempView("isSimpleWkt")
    val isSimpleDf = spark.sql("select st_geomFromWkt(wkt) as geos from isSimpleWkt").cache()
    isSimpleDf.createOrReplaceTempView("isSimpleDf")
    begin = System.nanoTime
    val isSimple = spark.sql("select st_isSimple(geos) from isSimpleDf")
    isSimple.createOrReplaceTempView("isSimple")
    spark.sql("CACHE table isSimple")
    end = System.nanoTime
    println("geomesa_st_isSimple_time: " + (end - begin) / 1e9d)

    // TODO: ST_GeometryType

    // TODO: ST_MakeValid

    // TODO: ST_SimplifyPreserveTopology

    // TODO: ST_PolygonFromEnvelope

    var containsData: Seq[(String, String)] = Nil
    for ( index <- 1 to cycleTimes) {
        containsData :+ ("POLYGON((-1 3,2 1,0 -3,-1 3))", "POLYGON((0 2,1 1,0 -1,0 2))")
    }
    val containsWkt = containsData.toDF("leftWkt", "rightWkt").cache()
    containsWkt.createOrReplaceTempView("containsWkt")
    val containsDf = spark.sql("select st_geomFromWkt(leftWkt) as left, st_geomFromWkt(rightWkt) as right from containsWkt").cache()
    containsDf.createOrReplaceTempView("containsDf")
    begin = System.nanoTime
    val contains = spark.sql("select st_contains(left, right) from containsDf")
    contains.createOrReplaceTempView("contains")
    spark.sql("CACHE table contains")
    end = System.nanoTime
    println("geomesa_st_contains_time: " + (end - begin) / 1e9d)

    var intersectsData: Seq[(String, String)] = Nil
    for ( index <- 1 to cycleTimes) {
        intersectsData :+ ("POINT(0 0)", "LINESTRING ( 0 0, 0 2 )")
    }
    val intersectsWkt = intersectsData.toDF("leftWkt", "rightWkt").cache()
    intersectsWkt.createOrReplaceTempView("intersectsWkt")
    val intersectsDf = spark.sql("select st_geomFromWkt(leftWkt) as left, st_geomFromWkt(rightWkt) as right from intersectsWkt").cache()
    intersectsDf.createOrReplaceTempView("intersectsDf")
    begin = System.nanoTime
    val intersects = spark.sql("select st_intersects(left, right) from intersectsDf")
    intersects.createOrReplaceTempView("intersects")
    spark.sql("CACHE table intersects")
    end = System.nanoTime
    println("geomesa_st_intersects_time: " + (end - begin) / 1e9d)

    var withinData: Seq[(String, String)] = Nil
    for ( index <- 1 to cycleTimes) {
        withinData :+ ("POLYGON((2 2, 7 2, 7 5, 2 5, 2 2))", "POLYGON((1 1, 8 1, 8 7, 1 7, 1 1))")
    }
    val withinWkt = withinData.toDF("leftWkt", "rightWkt").cache()
    withinWkt.createOrReplaceTempView("withinWkt")
    val withinDf = spark.sql("select st_geomFromWkt(leftWkt) as left, st_geomFromWkt(rightWkt) as right from withinWkt").cache()
    withinDf.createOrReplaceTempView("withinDf")
    begin = System.nanoTime
    val within = spark.sql("select st_within(left, right) from withinDf")
    within.createOrReplaceTempView("within")
    spark.sql("CACHE table within")
    end = System.nanoTime
    println("geomesa_st_within_time: " + (end - begin) / 1e9d)

    var distanceData: Seq[(String, String)] = Nil
    for ( index <- 1 to cycleTimes) {
        distanceData :+ ("POLYGON((-1 -1,2 2,0 1,-1 -1))", "POLYGON((5 2,7 4,5 5,5 2))")
    }
    val distanceWkt = distanceData.toDF("leftWkt", "rightWkt").cache()
    distanceWkt.createOrReplaceTempView("distanceWkt")
    val distanceDf = spark.sql("select st_geomFromWkt(leftWkt) as left, st_geomFromWkt(rightWkt) as right from distanceWkt").cache()
    distanceDf.createOrReplaceTempView("distanceDf")
    begin = System.nanoTime
    val distance = spark.sql("select st_distance(left, right) from distanceDf")
    distance.createOrReplaceTempView("distance")
    spark.sql("CACHE table distance")
    end = System.nanoTime
    println("geomesa_st_distance_time: " + (end - begin) / 1e9d)

    var areaData: Seq[(String)] = Nil
    for ( index <- 1 to cycleTimes) {
        areaData :+ "POLYGON((10 20,10 30,20 30,30 10))"
    }
    val areaWkt = areaData.toDF("wkt").cache()
    areaWkt.createOrReplaceTempView("areaWkt")
    val areaDf = spark.sql("select st_geomFromWkt(wkt) as geos from areaWkt").cache()
    areaDf.createOrReplaceTempView("areaDf")
    begin = System.nanoTime
    val area = spark.sql("select st_area(geos) from areaDf")
    area.createOrReplaceTempView("area")
    spark.sql("CACHE table area")
    end = System.nanoTime
    println("geomesa_st_area_time: " + (end - begin) / 1e9d)

    var centroidData: Seq[(String)] = Nil
    for ( index <- 1 to cycleTimes) {
        centroidData :+ "MULTIPOINT ( -1 0, -1 2, -1 3, -1 4, -1 7, 0 1, 0 3, 1 1, 2 0, 6 0, 7 8, 9 8, 10 6 )"
    }
    val centroidWkt = centroidData.toDF("wkt").cache()
    centroidWkt.createOrReplaceTempView("centroidWkt")
    val centroidDf = spark.sql("select st_geomFromWkt(wkt) as geos from centroidWkt").cache()
    centroidDf.createOrReplaceTempView("centroidDf")
    begin = System.nanoTime
    val centroid = spark.sql("select st_centroid(geos) from centroidDf")
    centroid.createOrReplaceTempView("centroid")
    spark.sql("CACHE table centroid")
    end = System.nanoTime
    println("geomesa_st_centroid_time: " + (end - begin) / 1e9d)

    var lengthData: Seq[(String)] = Nil
    for ( index <- 1 to cycleTimes) {
        lengthData :+ "LINESTRING(743238 2967416,743238 2967450,743265 2967450, 743265.625 2967416,743238 2967416)"
    }
    val lengthWkt = lengthData.toDF("wkt").cache()
    lengthWkt.createOrReplaceTempView("lengthWkt")
    val lengthDf = spark.sql("select st_geomFromWkt(wkt) as geos from lengthWkt").cache()
    lengthDf.createOrReplaceTempView("lengthDf")
    begin = System.nanoTime
    val length = spark.sql("select st_length(geos) from lengthDf")
    length.createOrReplaceTempView("length")
    spark.sql("CACHE table length")
    end = System.nanoTime
    println("geomesa_st_length_time: " + (end - begin) / 1e9d)

    var convexHullData: Seq[(String)] = Nil
    for ( index <- 1 to cycleTimes) {
        convexHullData :+ "GEOMETRYCOLLECTION(POINT(1 1),POINT(0 0))"
    }
    val convexHullWkt = convexHullData.toDF("wkt").cache()
    convexHullWkt.createOrReplaceTempView("convexHullWkt")
    val convexHullDf = spark.sql("select st_geomFromWkt(wkt) as geos from convexHullWkt").cache()
    convexHullDf.createOrReplaceTempView("convexHullDf")
    begin = System.nanoTime
    val convexHull = spark.sql("select st_convexHull(geos) from convexHullDf")
    convexHull.createOrReplaceTempView("convexHull")
    spark.sql("CACHE table convexHull")
    end = System.nanoTime
    println("geomesa_st_convexHull_time: " + (end - begin) / 1e9d)

    // TODO: ST_NPoints

    var envelopeData: Seq[(String)] = Nil
    for ( index <- 1 to cycleTimes) {
        envelopeData :+ "LINESTRING(77.29 29.07,77.42 29.26,77.27 29.31,77.29 29.07)"
    }
    val envelopeWkt = envelopeData.toDF("wkt").cache()
    envelopeWkt.createOrReplaceTempView("envelopeWkt")
    val envelopeDf = spark.sql("select st_geomFromWkt(wkt) as geos from envelopeWkt").cache()
    envelopeDf.createOrReplaceTempView("envelopeDf")
    begin = System.nanoTime
    val envelope = spark.sql("select st_envelope(geos) from envelopeDf")
    envelope.createOrReplaceTempView("envelope")
    spark.sql("CACHE table envelope")
    end = System.nanoTime
    println("geomesa_st_envelope_time: " + (end - begin) / 1e9d)

    // TODO: ST_Buffer, but there is st_bufferPoint udf in geomesa
  }
}
