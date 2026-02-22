package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {

    private final List<Map.Entry<String, Set<String>>> docs = new ArrayList<>();

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        // Build this document's unique word set from mapper values
        Set<String> uniqueWords = new HashSet<>(); // This doesn't need to be a set but that's what mapper does
        for (Text value : values) {
            String s = value.toString().trim();
            if (s.isEmpty()) continue;
            uniqueWords.addAll(Arrays.asList(s.split(",")));
        }

        // Compare w/ previous documents
        for (Map.Entry<String, Set<String>> e : docs) {
            double sim = jaccardSimilarity(uniqueWords, e.getValue());
            context.write(
                new Text(e.getKey() + ", " + key.toString() + " Similarity:"), // Keys are backwards so first doc is always first
                new Text(String.format("%.2f", sim))
            );
        }

        docs.add(new AbstractMap.SimpleEntry<>(key.toString(), uniqueWords));
    }

    private double jaccardSimilarity(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }
}
