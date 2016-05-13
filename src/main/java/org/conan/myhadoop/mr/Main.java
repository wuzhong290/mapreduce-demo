package org.conan.myhadoop.mr;

import org.apache.hadoop.mapred.JobConf;
import org.conan.myhadoop.hdfs.HdfsDAO;
import org.conan.myhadoop.matrix.MainRun;

import java.io.IOException;

/**
 * Created by wuzhong on 2016/5/13.
 */
public class Main {
    public static final String HDFS = "hdfs://master.spark.com:8020";

    public static JobConf config() {
        JobConf conf = new JobConf(Main.class);
        conf.setJobName("kpi");
        return conf;
    }

    public static void main(String[] args) {
        HdfsDAO hdfs = new HdfsDAO(MainRun.HDFS, config());
        try {
            hdfs.rmr(HDFS + "/user/hdfs");
            hdfs.mkdirs(HDFS + "/user/hdfs");
            hdfs.copyFile("logfile/access.log.10", HDFS + "/user/hdfs/log_kpi");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
