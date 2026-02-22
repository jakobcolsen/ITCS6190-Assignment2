package com.example;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DocumentSimilarityMapper extends Mapper<LongWritable, Text, Text, Text> {

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String content = value.toString();

        String[] words = content.split("\\s+"); // Split by word
        String id = words[0]; // First word is the document ID
        Set<String> uniqueWords = new HashSet<>(); // Only stores unique words
        for (String word : words) {
            if (word.equals(id)) {
                continue; // Skip the ID itself
            }
            uniqueWords.add(word);
        }

        // Output is {id, uniqueWords} as key-value
        context.write(new Text(id), new Text(String.join(",", uniqueWords)));
    }
    
}
