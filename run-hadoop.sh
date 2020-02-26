#!/bin/bash

# $HADOOP_PREFIX/bin/hdfs namenode -format	# do this only the first time
$HADOOP_PREFIX/sbin/hadoop-daemon.sh start namenode
$HADOOP_PREFIX/sbin/hadoop-daemon.sh start datanode

$HADOOP_PREFIX/sbin/yarn-daemon.sh start resourcemanager
$HADOOP_PREFIX/sbin/yarn-daemon.sh start nodemanager
