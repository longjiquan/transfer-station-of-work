下载Hadoop
wget https://mirrors.tuna.tsinghua.edu.cn/apache/hadoop/common/hadoop-2.7.7/hadoop-2.7.7.tar.gz

hadoop single node setup
参考链接：
https://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-common/SingleCluster.html
https://www.alexjf.net/blog/distributed-systems/hadoop-yarn-installation-definitive-guide/

解压压缩包
tar xvf hadoop-2.7.7.tar.gz

修改hadoop-env.sh，设置JAVA_HOME
cd hadoop-2.7.7/etc/hadoop/
vim hadoop-env.sh   # 添加如下一行，具体路径为java安装路径
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

修改~/.bashrc，设置HADOOP_PREFIX
vim ~/.bashrc   # 添加如下一行，路径为Hadoop所在路径
export HADOOP_PREFIX=/home/zilliz/hadoop-2.7.7
export HADOOP_HOME=$HADOOP_PREFIX
export HADOOP_COMMON_HOME=$HADOOP_PREFIX
export HADOOP_CONF_DIR=$HADOOP_PREFIX/etc/hadoop
export HADOOP_HDFS_HOME=$HADOOP_PREFIX
export HADOOP_MAPRED_HOME=$HADOOP_PREFIX
export HADOOP_YARN_HOME=$HADOOP_PREFIX
保存并退出vim
source ~/.bashrc
退出终端重新登入

vim $HADOOP_PREFIX/etc/hadoop/hdfs-site.xml # 修改为如下内容
```xml
<configuration>
        <property>
                <name>dfs.replication</name>
                <value>1</value>
        </property>
        <property>
                <name>dfs.datanode.data.dir</name>
                <value>file:///tmp/hadoop/hdfs/datanode</value>
        </property>
        <property>
                <name>dfs.namenode.name.dir</name>
                <value>file:///tmp/hadoop/hdfs/namenode</value>
        </property>
</configuration>
```

vim $HADOOP_PREFIX/etc/hadoop/core-site.xml #修改为如下内容
```xml
<configuration>
        <property>
                <name>fs.defaultFS</name>
                <value>hdfs://localhost:9000</value>
        </property>
</configuration>
```

vim $HADOOP_PREFIX/etc/hadoop/mapred-site.xml
```xml
<configuration>                                           
        <property>                                        
                <name>mapreduce.framework.name</name>     
                <value>yarn</value>                       
        </property>                                       
</configuration>                                          
```

vim $HADOOP_PREFIX/etc/hadoop/yarn-site.xml
```xml
<configuration>
        <property>
                <name>yarn.nodemanager.aux-services</name>
                <value>mapreduce_shuffle</value>
        </property>
</configuration>
```

无密访问
ssh-copy-id localhost

启动Hadoop
```shell
$HADOOP_PREFIX/bin/hdfs namenode -format    # 只在第一次时执行即可
$HADOOP_PREFIX/sbin/start-dfs.sh

$HADOOP_PREFIX/sbin/start-yarn.sh
```

输入jps看是否有对应的进程(namenode, datanode, nodemanager, resourcemanager)

初始化
```shell
$HADOOP_PREFIX/bin/hdfs dfs -mkdir /user
$HADOOP_PREFIX/bin/hdfs dfs -mkdir /user/zilliz
```

验证是否成功
```shell
$HADOOP_PREFIX/bin/hdfs dfs -put etc/hadoop input
$HADOOP_PREFIX/bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.4.jar grep input output 'dfs[a-z.]+'
$HADOOP_PREFIX/bin/hdfs dfs -get output output
cat output/*
```

spark配置
修改spark-env.sh，添加如下几行
```
export HADOOP_CONF_DIR=$HADOOP_PREFIX/etc/hadoop
export YARN_CONF_DIR=$HADOOP_PREFIX/etc/hadoop
```

提交任务（官网例子）
./bin/spark-submit --class org.apache.spark.examples.SparkPi --master yarn --driver-memory 4g --deploy-mode cluster --executor-memory 2g --executor-cores 1  examples/jars/spark-examples_2.12-3.0.0-preview2.jar 10


zookeeper
参考链接：https://zookeeper.apache.org/doc/current/zookeeperStarted.html

下载
wget https://mirrors.tuna.tsinghua.edu.cn/apache/zookeeper/stable/apache-zookeeper-3.5.7-bin.tar.gz

解压
tar xvf apache-zookeeper-3.5.7-bin.tar.gz

启动服务
cd apache-zookeeper-3.5.7-bin/conf
vim zoo.cfg     # 增加如下配置
```
tickTime=2000
dataDir=/var/lib/zookeeper  # 换成有权限的地方
clientPort=2181
```
cd ../bin
./bin/zkServer.sh start


accumulo
参考链接：https://accumulo.apache.org/quickstart-1.x/

下载
wget http://mirror.bit.edu.cn/apache/accumulo/1.9.3/accumulo-1.9.3-bin.tar.gz

解压
tar xvf accumulo-1.9.3-bin.tar.gz

编译
./bin/build_native_library.sh

配置
./bin/bootstrap_config.sh
（选择是321）

修改conf/accumulo-site.xml
instance.volumes改为hdfs://localhost:9000/tmp/accumulo

修改conf/accumulo-env.sh
export ZOOKEEPER_HOME=/home/zilliz/apache-zookeeper-3.5.7-bin/lib
export HADOOP_PREFIX=/home/zilliz/hadoop-2.7.7
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/zilliz/accumulo-1.9.3/lib/native

初始化
./bin/accumulo init
instance: zilliz
password: zilliz

启动服务
./bin/start-all.sh

创建用户zilliz
./bin/accumulo shell -u root
>> createuser zilliz
>> exit

Note: 下面这段被注释掉的是不靠谱的官网步骤，请直接跳过。
// geomesa
// 参考链接：
// https://www.geomesa.org/documentation/tutorials/geomesa-examples-gdelt.html
// https://www.geomesa.org/documentation/tutorials/spark.html
// https://www.geomesa.org/documentation/user/accumulo/install.html#setting-up-accumulo-commandline
// 
// 下载
// wget https://github.com/locationtech/geomesa/releases/download/geomesa_2.11-2.4.0/geomesa-accumulo_2.11-2.4.0-bin.tar.gz
// 
// 解压
// tar xvf geomesa-accumulo_2.11-2.4.0-bin
// 
// 拷贝jar包
// cp geomesa-accumulo_2.11-2.4.0/dist/accumulo/geomesa-accumulo-distributed-runtime_2.11-2.4.0.jar accumulo-1.9.3/lib/ext/
// 
// 创建namespace
// ./bin/setup-namespace.sh -u root -n zilliz
// 
// 配置环境变量
// ./bin/geomesa-accumulo configure
// 根据提示输入即可，注意，按照提示说的更新.bashrc
// 
// ```这里不需要了
// 安装对应jar包
// vim bin/install-hadoop-accumulo.sh
// 修改对应依赖的版本，分别是：
// accumulo_version="1.9.3"
// hadoop_version="2.7.7"
// zookeeper_version="3.5.7"
// 
// ./bin/install-hadoop-accumulo.sh
// ```
// 
// 安装其他依赖
// ./bin/install-jai.sh
// ./bin/install-jline.sh
// 
// 下载测试数据
// ./bin/download-data.sh
// 输入想下载数据的日期
// 
// 插入accumulo数据库
// geomesa-accumulo ingest -u USERNAME -c CATALOGNAME -s gdelt -C gdelt gdelt_data.csv
// 这里USERNAME为`root`（之前是zilliz），CATALOGNAME为表名，将在后面的测试代码里面用到，csv文件为上述下载的
// 
// 测试
// git clone https://github.com/geomesa/geomesa-tutorials.git
// cd geomesa-tutorials/geomesa-examples-spark
// 
// 修改CountByDay.scala里面的代码，
// instanceId->zilliz
// zookeepes->localhost
// user->root
// password->zilliz
// tableName->zilliz.gdelt（上述提到的）
// 
// 编译
// mvn clean install
// 
// 运行
// /home/zilliz/hadoop_spark3_p2/bin/spark-submit --master yarn --class com.example.geomesa.spark.CountByDay target/geomesa-examples-spark-2.5.0-SNAPSHOT.jar --jars file://path/to/geomesa-accumulo-spark-runtime_2.11-$VERSION.jar
// 
// --jars的选项可以改为修改spark的配置文件，在spark-default.conf里添加以下两行：
// ```
// spark.driver.extraClassPath /home/zilliz/geomesa-accumulo_2.11-2.4.0/lib/geomesa-accumulo-spark_2.11-2.4.0.jar:/home/zilliz/geomesa-accumulo_2.11-2.4.0/lib/geomesa-index-api_2.11-2.4.0.jar
// spark.executor.extraClassPath /home/zilliz/geomesa-accumulo_2.11-2.4.0/lib/geomesa-accumulo-spark_2.11-2.4.0.jar:/home/zilliz/geomesa-accumulo_2.11-2.4.0/lib/geomesa-index-api_2.11-2.4.0.jar
// ```
// 
// 另外，如果在分布式环境下，需要额外添加如下两行：
// ```
// spark.serializer        org.apache.spark.serializer.KryoSerializer
// spark.kryo.registrator  org.locationtech.geomesa.spark.GeoMesaSparkKryoRegistrator
// ```

下面的教程才是靠谱教程：
Note：下面需要手动创建maven项目，代码可在https://github.com/dragondriver/transfer-station-of-work 找到。

pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>geomesa-spark-test</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <dependencies>
	    <!-- https://mvnrepository.com/artifact/org.scala-lang/scala-library -->
	    <dependency>
		    <groupId>org.scala-lang</groupId>
		    <artifactId>scala-library</artifactId>
		    <version>2.12.8</version>
	    </dependency>
            <!-- https://mvnrepository.com/artifact/org.scala-lang/scala-reflect -->
	    <dependency>
		    <groupId>org.scala-lang</groupId>
		    <artifactId>scala-reflect</artifactId>
		    <version>2.12.8</version>
	    </dependency>
            <!-- https://mvnrepository.com/artifact/org.scala-lang/scala-compiler -->
	    <dependency>
		    <groupId>org.scala-lang</groupId>
		    <artifactId>scala-compiler</artifactId>
		    <version>2.12.8</version>
	    </dependency>
	    <!-- https://mvnrepository.com/artifact/org.apache.spark/spark-core -->
	    <dependency>
		    <groupId>org.apache.spark</groupId>
		    <artifactId>spark-core_2.12</artifactId>
		    <version>3.0.0-preview2</version>
	    </dependency>
	    <!-- https://mvnrepository.com/artifact/org.apache.spark/spark-sql -->
	    <dependency>
		    <groupId>org.apache.spark</groupId>
		    <artifactId>spark-sql_2.12</artifactId>
		    <version>3.0.0-preview2</version>
	    </dependency>
	    <!-- https://mvnrepository.com/artifact/org.locationtech.geomesa/geomesa-spark-jts -->
	    <dependency>
		    <groupId>org.locationtech.geomesa</groupId>
		    <artifactId>geomesa-spark-jts_2.11</artifactId>
		    <version>2.1.1</version>
	    </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.scala-tools</groupId>
                <artifactId>maven-scala-plugin</artifactId>
                <version>2.15.2</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>testCompile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

</project>
```

目录结构如下：
```
├── jts-example.csv
├── pom.xml
└── src
    └── main
        ├── java
        └── scala
            └── GeomesaDemo.scala
```

GeomesaDemo.scala代码，注意代码中CSV路径根据具体情况替换。
```scala
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
```

编译
```shell
mvn clean scala:compile compile package
```

修改spark-default.conf，添加如下两行：
```
spark.driver.extraClassPath /home/zilliz/.m2/repository/org/locationtech/geomesa/geomesa-spark-jts_2.11/2.1.1/geomesa-spark-jts_2.11-2.1.1.jar:/home/zilliz/.m2/repository/com/vividsolutions/jts-core/1.14.0/jts-core-1.14.0.jar:/home/zilliz/.m2/repository/com/vividsolutions/jts-io/1.14.0/jts-io-1.14.0.jar:/home/zilliz/.m2/repository/org/locationtech/spatial4j/spatial4j/0.7/spatial4j-0.7.jar
spark.executor.extraClassPath /home/zilliz/.m2/repository/org/locationtech/geomesa/geomesa-spark-jts_2.11/2.1.1/geomesa-spark-jts_2.11-2.1.1.jar:/home/zilliz/.m2/repository/com/vividsolutions/jts-core/1.14.0/jts-core-1.14.0.jar:/home/zilliz/.m2/repository/com/vividsolutions/jts-io/1.14.0/jts-io-1.14.0.jar:/home/zilliz/.m2/repository/org/locationtech/spatial4j/spatial4j/0.7/spatial4j-0.7.jar
```

运行spark任务
```shell
spark-submit --master yarn --class GeomesaDemo /home/zilliz/geomesa-demo-mvn/target/geomesa-spark-test-1.0-SNAPSHOT.jar
```

如果一切成功，有如下输出：
```
+-----+-------------+--------------------+--------+---------+
| name|    pointText|         polygonText|latitude|longitude|
+-----+-------------+--------------------+--------+---------+
|itemA|Point (40 40)|Polygon ((35 35, ...|    40.0|     40.0|
|itemB|Point (30 30)|Polygon ((25 25, ...|    30.0|     30.0|
|itemC|Point (20 20)|Polygon ((15 15, ...|    20.0|     20.0|
+-----+-------------+--------------------+--------+---------+

+-----+-------------+--------------------+--------+---------+--------------------+-------------+
| name|    pointText|         polygonText|latitude|longitude|             polygon|        point|
+-----+-------------+--------------------+--------+---------+--------------------+-------------+
|itemA|Point (40 40)|Polygon ((35 35, ...|    40.0|     40.0|POLYGON ((35 35, ...|POINT (40 40)|
|itemB|Point (30 30)|Polygon ((25 25, ...|    30.0|     30.0|POLYGON ((25 25, ...|POINT (30 30)|
|itemC|Point (20 20)|Polygon ((15 15, ...|    20.0|     20.0|POLYGON ((15 15, ...|POINT (20 20)|
+-----+-------------+--------------------+--------+---------+--------------------+-------------+
```