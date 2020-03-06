参考链接：
https://hadoop.apache.org/docs/r2.7.6/hadoop-project-dist/hadoop-common/ClusterSetup.html
https://www.alexjf.net/blog/distributed-systems/hadoop-yarn-installation-definitive-guide/#cluster-installation

对比单节点部署的区别是：

1，core-site.xml中的fs.defaultFS改为运行namenode节点的host，如下：
```xml
<configuration>
        <property>
                <name>fs.defaultFS</name>
                <value>hdfs://spark1:9000</value>
        </property>
</configuration>
```

2，yarn-site.xml中需要修改resourcemanager中的hostname，如下：
```xml
<configuration>
        <property>
                <name>yarn.nodemanager.aux-services</name>
                <value>mapreduce_shuffle</value>
        </property>

        <property>
                <name>yarn.resourcemanager.hostname</name>
                <value>spark1</value>
        </property>
</configuration>
```

3，配置slave，编辑$HADOOP_PREFIX/etc/hadoop/slaves，添加如下节点：
```
spark1
spark4
```
注意，上述hostname需要在所有节点/etc/hosts里面添加对应的ip映射，如下：
```
10.0.5.5 spark1 
10.0.5.6 spark2 
10.0.5.7 spark3 
10.0.5.8 spark4 
```

4，针对上述slaves，确保namenode与各个slaves之间能够ssh无密访问，操作如下：
```shell
ssh-copy-id spark1
ssh-copy-id spark4
ssh spark4  # 登录到slave节点执行同样操作
ssh-copy-id spark1
ssh-copy-id spark4
```

5，启动
在namenode节点执行以下命令：
```shell
$HADOOP_PREFIX/bin/hdfs namenode -format    # 初始化，只执行一次
$HADOOP_PREFIX/sbin/start-dfs.sh
$HADOOP_PREFIX/sbin/start-yarn.sh
```

6，在各个节点输入jps看是否有对应进程启动，
spark1应该有namenode，resourcemanager，datanode，nodemanager,
spark4应该有datanode，nodemanager，


7，提交spark任务，
spark-submit --class org.apache.spark.examples.SparkPi --master yarn /home/zilliz/hadoop_spark3_p2/examples/jars/spark-examples_2.12-3.0.0-preview2.jar 10

8，目前遇到的问题是，yarn集群能跑起来，但是提交spark任务时，出现UNDEFINED status行为，另外报的异常里面也有提示send rpc failed.