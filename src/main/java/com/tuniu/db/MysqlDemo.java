package com.tuniu.db;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;

/**
 * Created by wuzhong on 2016/4/29.
 */
public class MysqlDemo {

    public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf,args).getRemainingArgs();
        //DistributedCache.addFileToClassPath(new Path("hdfs://master.spark.com:8020/hdfsPath/mysql-connector-java-5.1.35.jar"), conf);
        //先删除output目录
        deleteDir(conf, "hdfs://master.spark.com:8020/apps/output");
        DBConfiguration.configureDB(conf, "com.mysql.jdbc.Driver",
                "jdbc:mysql://10.10.30.200:3306/d_mob?zeroDateTimeBehavior=convertToNull", "mobtest", "tuniu520");

        Job job = Job.getInstance(conf,"mysqldemo");
        FileOutputFormat.setOutputPath(job, new Path("hdfs://master.spark.com:8020/apps/output"));
        String [] fields = {"id", "name", "gender", "number"};
        DBInputFormat.setInput(job, StudentRecord.class, "Student", null, "id", fields);
        job.setJarByClass(MysqlDemo.class);
        job.setMapperClass(DBAccessMapper.class);
        job.setCombinerClass(IdentityReducer.class);
        job.setReducerClass(IdentityReducer.class);
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(DBInputFormat.class);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }


    private static void deleteDir(Configuration conf, String dirPath) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        Path targetPath = new Path(dirPath);
        if (fs.exists(targetPath)) {
            boolean delResult = fs.delete(targetPath, true);
            if (delResult) {
                System.out.println(targetPath + " has been deleted sucessfullly.");
            } else {
                System.out.println(targetPath + " deletion failed.");
            }
        }

    }

    public static class DBAccessMapper extends Mapper<LongWritable, StudentRecord, LongWritable, Text>{
        @Override
        protected void map(LongWritable key, StudentRecord value, Context context) throws IOException, InterruptedException {
            context.write(new LongWritable(value.id), new Text(value
                    .toString()));
        }
    }

    public static class IdentityReducer extends Reducer<LongWritable,Text,LongWritable,Text>{
        @Override
        protected void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            context.write(key,values.iterator().next());
        }
    }
}
