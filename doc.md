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