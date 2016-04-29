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

hadoop --config /etc/hadoop/conf jar mapreduce-demo-job.jar -libjars mysql-connector-java-5.1.35.jar
