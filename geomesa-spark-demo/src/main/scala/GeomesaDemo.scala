import org.apache.spark.sql.types._
import org.locationtech.geomesa.spark.jts._
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql._
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object GeomesaDemo {
  def main(args: Array[String]) {
    val spark = SparkSession.builder()
                  .appName("geomesa-demo")
                  .getOrCreate()
                  .withJTS

    val schema = StructType(Array(
      StructField("name",StringType, nullable=false),
      StructField("pointText", StringType, nullable=false),
      StructField("polygonText", StringType, nullable=false),
      StructField("latitude", DoubleType, nullable=false),
      StructField("longitude", DoubleType, nullable=false)))

    // val dataFile = this.getClass.getClassLoader.getResource("jts-example.csv").getPath
    val dataFile = "file:///home/zilliz/geomesa-demo-mvn/jts-example.csv"
    val df = spark.read
      .schema(schema)
      .option("sep", "-")
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .csv(dataFile)
    df.show()

    val alteredDF = df
      .withColumn("polygon", st_polygonFromText(col("polygonText")))
      .withColumn("point", st_makePoint(col("latitude"), col("longitude")))
    alteredDF.show()
  }
}
