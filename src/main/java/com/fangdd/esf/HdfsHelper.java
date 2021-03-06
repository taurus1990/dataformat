package com.fangdd.esf;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lijiang on 5/27/17.
 */
public class HdfsHelper {

    /*online hdfs*/
    public static String hdfsFS = "hdfs://10.50.23.210:8020";
    /*test hdfs*/
//    public static String hdfsFS = "hdfs://10.12.21.131/:8020";
    public static String hdfsConf = "resources/hdfs-site.xml";
    public static String coreConf = "resources/core-site.xml";
    public static String implconf = "org.apache.hadoop.hdfs.DistributedFileSystem";

    public static Configuration conf = new Configuration();
    static {
        Configuration conf = new Configuration();
        conf.addResource(hdfsConf);
        conf.addResource(coreConf);
        conf.set("fs.hdfs.impl",implconf);
    }


    public static boolean mkdir(String path) throws IOException {

        Path srcpath = new Path(path);
        FileSystem fileSystem = FileSystem.get(URI.create(hdfsFS), conf);

        boolean stats = fileSystem.mkdirs(srcpath);
        fileSystem.close();
        return stats;
    }

    public static void delete(String path) throws IOException {
        Path srcpath = new Path(path);
        FileSystem fileSystem = FileSystem.get(URI.create(hdfsFS), conf);
        boolean stats = fileSystem.deleteOnExit(srcpath);
        if (stats) {
            System.out.println("delete ok");
        } else {
            System.out.println("delete failed");
        }
        fileSystem.close();
    }


    public static List<String> reader(Path filePath) throws IOException {

        FileSystem fileSystem = FileSystem.get(URI.create(hdfsFS), conf);
        FSDataInputStream hdfsInStream = fileSystem.open(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(hdfsInStream));
        List<String> contents = new ArrayList<String>();
        String line;
        while ((line = reader.readLine()) != null) {
            contents.add(line);
        }

        hdfsInStream.close();
        reader.close();

        fileSystem.close();
        return contents;
    }

    public static List<String> reader(String srcPath, String encoding) throws IOException {

        Path filePath = new Path(srcPath);
        FileSystem fileSystem = FileSystem.get(URI.create(hdfsFS), conf);
        FSDataInputStream hdfsInStream = fileSystem.open(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(hdfsInStream, encoding));
        List<String> contents = new ArrayList<String>();
        String line;
        while ((line = reader.readLine()) != null) {
            contents.add(line);
        }

        hdfsInStream.close();
        reader.close();

        fileSystem.close();
        return contents;
    }


    public static void writer(String srcPath, List<String> contents) throws IOException {

        Path filePath = new Path(srcPath);
        FileSystem fileSystem = FileSystem.get(URI.create(hdfsFS), conf);
        FSDataOutputStream outputStream = fileSystem.create(filePath);
        for (String item : contents) {
            item = item + "\n";
            outputStream.write(item.getBytes("UTF-8"));
        }
        outputStream.close();
        fileSystem.close();

    }

    public static void move(String srcPath, String tarDir) throws IOException {

        String tarpath = tarDir + new Path(srcPath).getName();
        FileSystem fileSystem = FileSystem.get(URI.create(hdfsFS), conf);
        boolean status = fileSystem.rename(new Path(srcPath),new Path(tarpath));
        if(status){
            System.out.println("源数据备份成功");
        }else{
            System.out.println("源数据备份失败");
        }
        fileSystem.close();

    }

    public static FileStatus[] list(String path) throws Exception {

        Path srcpath = new Path(path);
        FileSystem fileSystem = FileSystem.get(URI.create(hdfsFS), conf);
        FileStatus[] stats = fileSystem.listStatus(srcpath);
        fileSystem.close();
        return stats;

    }


}
