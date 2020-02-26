#!/bin/bash

# $HADOOP_PREFIX/bin/hdfs namenode -format	# do this only the first time
$HADOOP_PREFIX/sbin/hadoop-daemon.sh stop namenode
$HADOOP_PREFIX/sbin/hadoop-daemon.sh stop datanode

$HADOOP_PREFIX/sbin/yarn-daemon.sh stop resourcemanager
$HADOOP_PREFIX/sbin/yarn-daemon.sh stop nodemanager
