import org.apache.spark.sql.types._
import org.apache.spark.sql._
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.locationtech.geomesa.spark.jts._

object GeomesaSparkUDFDemo {
  def main(args: Array[String]) {
    val spark = SparkSession.builder()
                  .appName("geomesa-demo")
                  .getOrCreate()
                  .withJTS

    val testWktDf = spark.read.json("/user/zilliz/test.json").cache()
    testWktDf.createOrReplaceTempView("testWktDf")
    val testGeomDf = spark.sql("select st_geomFromWKT(left) as leftGeom, st_geomFromWKT(right) as rightGeom from testWktDf").cache()
    testGeomDf.createOrReplaceTempView("testGeomDf")
    val containsDf = spark.sql("select st_contains(leftGeom, rightGeom) from testGeomDf")
    containsDf.createOrReplaceTempView("containsDf")
    spark.sql("select * from containsDf").show()

    val someDF = Seq(
      ("left", "right"),
      ("left", "right"),
      ("left", "right"))

    import spark.implicits._
    someDF.toDF("left", "right").show()

  }
}
