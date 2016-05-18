package com.hadoop.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by wuzhong on 2016/5/3.
 */
public class MapReduceUtil {

    private static final Log LOG = LogFactory.getLog(MapReduceUtil.class);

    public static void addDependencyJars(Job job) throws IOException {
        try {
            addDependencyJars(job.getConfiguration(),
                    // when making changes here, consider also mapred.TableMapReduceUtil
                    // pull job classes
                    job.getMapOutputKeyClass(),
                    job.getMapOutputValueClass(),
                    job.getInputFormatClass(),
                    job.getOutputKeyClass(),
                    job.getOutputValueClass(),
                    job.getOutputFormatClass(),
                    job.getPartitionerClass(),
                    job.getCombinerClass());
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    public static void addDependencyJars(Configuration conf,
                                         Class<?>... classes) throws IOException {

        FileSystem localFs = FileSystem.getLocal(conf);
        Set<String> jars = new HashSet<String>();
        // Add jars that are already in the tmpjars variable
        jars.addAll(conf.getStringCollection("tmpjars"));

        // add jars as we find them to a map of contents jar name so that we can avoid
        // creating new jars for classes that have already been packaged.
        Map<String, String> packagedClasses = new HashMap<String, String>();

        // Add jars containing the specified classes
        for (Class<?> clazz : classes) {
            if (clazz == null) continue;

            Path path = findOrCreateJar(clazz, localFs, packagedClasses);
            if (path == null) {
                LOG.warn("Could not find jar for class " + clazz +
                        " in order to ship it to the cluster.");
                continue;
            }
            if (!localFs.exists(path)) {
                LOG.warn("Could not validate jar file " + path + " for class "
                        + clazz);
                continue;
            }
            jars.add(path.toString());
        }
        if (jars.isEmpty()) return;

        conf.set("tmpjars", StringUtils.arrayToString(jars.toArray(new String[jars.size()])));
    }

    private static Path findOrCreateJar(Class<?> my_class, FileSystem fs,
                                        Map<String, String> packagedClasses)
            throws IOException {
        // attempt to locate an existing jar for the class.
        String jar = findContainingJar(my_class, packagedClasses);
        if (null == jar || jar.isEmpty()) {
            jar = getJar(my_class);
            updateMap(jar, packagedClasses);
        }

        if (null == jar || jar.isEmpty()) {
            return null;
        }

        LOG.debug(String.format("For class %s, using jar %s", my_class.getName(), jar));
        return new Path(jar).makeQualified(fs);
    }

    private static String findContainingJar(Class<?> my_class, Map<String, String> packagedClasses)
            throws IOException {
        ClassLoader loader = my_class.getClassLoader();

        String class_file = my_class.getName().replaceAll("\\.", "/") + ".class";

        if (loader != null) {
            // first search the classpath
            for (Enumeration<URL> itr = loader.getResources(class_file); itr.hasMoreElements();) {
                URL url = itr.nextElement();
                if ("jar".equals(url.getProtocol())) {
                    String toReturn = url.getPath();
                    if (toReturn.startsWith("file:")) {
                        toReturn = toReturn.substring("file:".length());
                    }
                    // URLDecoder is a misnamed class, since it actually decodes
                    // x-www-form-urlencoded MIME type rather than actual
                    // URL encoding (which the file path has). Therefore it would
                    // decode +s to ' 's which is incorrect (spaces are actually
                    // either unencoded or encoded as "%20"). Replace +s first, so
                    // that they are kept sacred during the decoding process.
                    toReturn = toReturn.replaceAll("\\+", "%2B");
                    toReturn = URLDecoder.decode(toReturn, "UTF-8");
                    return toReturn.replaceAll("!.*$", "");
                }
            }
        }

        // now look in any jars we've packaged using JarFinder. Returns null when
        // no jar is found.
        return packagedClasses.get(class_file);
    }

    private static String getJar(Class<?> my_class) {
        String ret = null;
        try {
            ret = JarFinder.getJar(my_class);
        } catch (Exception e) {
            // toss all other exceptions, related to reflection failure
            throw new RuntimeException("getJar invocation failed.", e);
        }

        return ret;
    }

    private static void updateMap(String jar, Map<String, String> packagedClasses) throws IOException {
        if (null == jar || jar.isEmpty()) {
            return;
        }
        ZipFile zip = null;
        try {
            zip = new ZipFile(jar);
            for (Enumeration<? extends ZipEntry> iter = zip.entries(); iter.hasMoreElements();) {
                ZipEntry entry = iter.nextElement();
                if (entry.getName().endsWith("class")) {
                    packagedClasses.put(entry.getName(), jar);
                }
            }
        } finally {
            if (null != zip) zip.close();
        }
    }
}
