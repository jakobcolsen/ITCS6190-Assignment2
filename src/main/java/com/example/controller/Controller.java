package com.example.controller;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.example.DocumentSimilarityMapper;
import com.example.DocumentSimilarityReducer;

// Copied from HandsOn4 and modified for this assignment
public class Controller {

    public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "document similarity");
        job.setJarByClass(Controller.class);
        job.setMapperClass(DocumentSimilarityMapper.class);
        job.setReducerClass(DocumentSimilarityReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setNumReduceTasks(1);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}