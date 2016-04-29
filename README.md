# mapreduce-demo
一个mapreduce项目

在IntelliJ IDEA里面进行开发调试的开发环境
一、需要准备的资源
下载hadoop-2.6.0.tar.gz，解压缩后设置环境变量HADOOP_HOME为解压缩目录，
%HADOOP_HOME%\bin加入path中，将解压缩目录的bin文件夹中的hadoop.dll放入C:\Windows\System32
二、具体步骤
1、创建一个maven项目mapreduce-demo
2、添加依赖hadoop-2.6.0：add-hadoop-lib.png、add-hadoop-lib-all.png
3、增加一个运行配置：configurations.png
    其中working directory设置为hadoop-2.6.0.tar.gz的解压缩目录
这样就可以在本机上调试mapreduce程序了。

我基于的环境为cdh5.7.0，

一、第三方jar的引用方式步骤（这种方式不够灵活）：
1、mysql-connector-java-5.1.35.jar放入$HADOOP_HOME/lib下面（如果是CDH安装$HADOOP_HOME=/opt/cloudera/parcels/CDH/lib/hadoop/lib）
2、重新启动：HDFS、MapReduce 生效
二、export HADOOP_CLASSPATH=/home/hadooplib不起作用

三、-libjars无效
hadoop jar mapreduce-demo-job.jar -libjars /home/hadooplib/mysql-connector-java-5.1.35.jar
hadoop jar mapreduce-demo-job.jar -files /home/hadooplib/mysql-connector-java-5.1.35.jar


四、fatjar无效


通过命令行参数传递jar文件, 如-libjars等;
直接在conf中设置, 如conf.set(“tmpjars”,*.jar), jar文件用逗号隔开;
利用分布式缓存, 如DistributedCache.addArchiveToClassPath(path, job), 此处的path必须是hdfs, 即自己讲jar上传到hdfs上, 然后将路径加入到分布式缓存中;
第三方jar文件和自己的程序打包到一个jar文件中, 程序通过job.getJar()将获得整个文件并将其传至hdfs上. (很笨重)
在每台机器的$HADOOP_HOME/lib目录中加入jar文件. (不推荐)



[root@master wuzhong]# hadoop jar mapreduce-demo-job.jar
16/04/29 11:57:27 INFO mapred.JobClient: Running job: job_201604291156_0001
16/04/29 11:57:28 INFO mapred.JobClient:  map 0% reduce 0%
16/04/29 11:57:45 INFO mapred.JobClient:  map 50% reduce 0%
16/04/29 11:57:48 INFO mapred.JobClient:  map 100% reduce 0%
16/04/29 11:57:55 INFO mapred.JobClient:  map 100% reduce 100%
16/04/29 11:57:56 INFO mapred.JobClient: Job complete: job_201604291156_0001
16/04/29 11:57:56 INFO mapred.JobClient: Counters: 31
16/04/29 11:57:56 INFO mapred.JobClient:   File System Counters
16/04/29 11:57:56 INFO mapred.JobClient:     FILE: Number of bytes read=70
16/04/29 11:57:56 INFO mapred.JobClient:     FILE: Number of bytes written=644055
16/04/29 11:57:56 INFO mapred.JobClient:     FILE: Number of read operations=0
16/04/29 11:57:56 INFO mapred.JobClient:     FILE: Number of large read operations=0
16/04/29 11:57:56 INFO mapred.JobClient:     FILE: Number of write operations=0
16/04/29 11:57:56 INFO mapred.JobClient:     HDFS: Number of bytes read=156
16/04/29 11:57:56 INFO mapred.JobClient:     HDFS: Number of bytes written=45
16/04/29 11:57:56 INFO mapred.JobClient:     HDFS: Number of read operations=2
16/04/29 11:57:56 INFO mapred.JobClient:     HDFS: Number of large read operations=0
16/04/29 11:57:56 INFO mapred.JobClient:     HDFS: Number of write operations=1
16/04/29 11:57:56 INFO mapred.JobClient:   Job Counters
16/04/29 11:57:56 INFO mapred.JobClient:     Launched map tasks=2
16/04/29 11:57:56 INFO mapred.JobClient:     Launched reduce tasks=1
16/04/29 11:57:56 INFO mapred.JobClient:     Total time spent by all maps in occupied slots (ms)=18684
16/04/29 11:57:56 INFO mapred.JobClient:     Total time spent by all reduces in occupied slots (ms)=4001
16/04/29 11:57:56 INFO mapred.JobClient:     Total time spent by all maps waiting after reserving slots (ms)=0
16/04/29 11:57:56 INFO mapred.JobClient:     Total time spent by all reduces waiting after reserving slots (ms)=0
16/04/29 11:57:56 INFO mapred.JobClient:   Map-Reduce Framework
16/04/29 11:57:56 INFO mapred.JobClient:     Map input records=2
16/04/29 11:57:56 INFO mapred.JobClient:     Map output records=2
16/04/29 11:57:56 INFO mapred.JobClient:     Map output bytes=57
16/04/29 11:57:56 INFO mapred.JobClient:     Input split bytes=156
16/04/29 11:57:56 INFO mapred.JobClient:     Combine input records=2
16/04/29 11:57:56 INFO mapred.JobClient:     Combine output records=2
16/04/29 11:57:56 INFO mapred.JobClient:     Reduce input groups=2
16/04/29 11:57:56 INFO mapred.JobClient:     Reduce shuffle bytes=87
16/04/29 11:57:56 INFO mapred.JobClient:     Reduce input records=2
16/04/29 11:57:56 INFO mapred.JobClient:     Reduce output records=2
16/04/29 11:57:56 INFO mapred.JobClient:     Spilled Records=4
16/04/29 11:57:56 INFO mapred.JobClient:     CPU time spent (ms)=7990
16/04/29 11:57:56 INFO mapred.JobClient:     Physical memory (bytes) snapshot=1057763328
16/04/29 11:57:56 INFO mapred.JobClient:     Virtual memory (bytes) snapshot=5090312192
16/04/29 11:57:56 INFO mapred.JobClient:     Total committed heap usage (bytes)=892862464



CREATE TABLE `Student` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(32) NOT NULL DEFAULT '' COMMENT 'name',
  `gender` VARCHAR(32) NOT NULL DEFAULT '' COMMENT 'gender',
  `number` VARCHAR(32) NOT NULL DEFAULT '' COMMENT 'number',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='Student';

    id  name    gender   number
------  ------  -------  ---------
     1  name    gender   number
     2  name1   gender2  number2

[root@master wuzhong]# hadoop fs -cat /apps/output/part-r-00000
1       name gender number
2       name1 gender2 number2



export HADOOP_ROOT_LOGGER=DEBUG,console
export HADOOP_ROOT_LOGGER=INFO,console

export HADOOP_USE_CLIENT_CLASSLOADER=""
export HADOOP_USE_CLIENT_CLASSLOADER=true


