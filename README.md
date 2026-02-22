# Assignment 2: Document Similarity using MapReduce

**Name:** Jakob Olsen 

**Student ID:** 801341195

## Approach and Implementation

### Mapper Design
The mapper takes in a LongWritable key, with a Text value. The LongWritable key is discarded, as the desired output simply uses the document name, which is store in the value.

The mapper splits the Text value into separate words, and sets the first word to be the ID. In our input, you'll note that the first word is `DocumentX`, with X being the number of the entry.

The mapper uses a hash set to efficiently determine the unique words of the given document. 

The mapper returns a new key value pair, with the DocumentID being the key, and a list of the unique words printed as a string as the value.

### Reducer Design
The Reducer takes in a Text key, a Text value, and returns a Text key and a Text value. 

The reducer first puts the value, which is the unique words returned from the mapper, and adds them to another set. By putting them in a set, it is easier to compute Jaccard Similarity. 

The reducer then compares the given key, value pair with previous pairs, which are stored in a list of map entries. The map stores the key, value pair. For each previous entry, the Jaccard Similarity is calculated, and a new key, value pair is added to the output. The key is a string denoting which documents are being compared, and the value is the result of the Jaccard Similarity computation, stored as a string.

### Overall Data Flow
Data is read by the Hadoop cluster and handed to the mapper function. The mapper function is responsible for associating the set of unique words for a document, and the ID of the document. The mapper returns a key, value pair of the DocumentID and the list of unique words.

The values are then shuffled and sorted, which puts the documents in order. This can be seen by how the output is given in the text file. Where Document1 comes first, then 2, then 3 in order of appearence. 

The reducer takes these values, and is responsible for comparing them with previously seen documents, and outputting the Jaccard Similarity as a new key value pair. Where the key states the comparison being made, and the value is the result of the Jaccard Similarity computation.

The data is then written to an output file.
---

## Setup and Execution

### 1. **Start the Hadoop Cluster**

Run the following command to start the Hadoop cluster:

```bash
docker compose up -d
```

### 2. **Build the Code**

Build the code using Maven:

```bash
mvn clean package
```

### 4. **Copy JAR to Docker Container**

Copy the JAR file to the Hadoop ResourceManager container:

```bash
docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 5. **Move Dataset to Docker Container**

Copy the dataset to the Hadoop ResourceManager container:

```bash
docker cp shared-folder/input/data/input.txt resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 6. **Connect to Docker Container**

Access the Hadoop ResourceManager container:

```bash
docker exec -it resourcemanager /bin/bash
```

Navigate to the Hadoop directory:

```bash
cd /opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 7. **Set Up HDFS**

Create a folder in HDFS for the input dataset:

```bash
hadoop fs -mkdir -p /input/data
```

Copy the input dataset to the HDFS folder:

```bash
hadoop fs -put ./input.txt /input/data
```

### 8. **Execute the MapReduce Job**

Run your MapReduce job using the following command: Here I got an error saying output already exists so I changed it to output1 instead as destination folder

```bash
hadoop jar /opt/hadoop-3.2.1/share/hadoop/mapreduce/DocumentSimilarity-0.0.1-SNAPSHOT.jar com.example.controller.Controller /input/data/input.txt /output1
```

### 9. **View the Output**

To view the output of your MapReduce job, use:

```bash
hadoop fs -cat /output1/*
```

### 10. **Copy Output from HDFS to Local OS**

To copy the output from HDFS to your local machine:

1. Use the following command to copy from HDFS:
    ```bash
    hdfs dfs -get /output1 /opt/hadoop-3.2.1/share/hadoop/mapreduce/
    ```

2. use Docker to copy from the container to your local machine:
   ```bash
   exit 
   ```
    ```bash
    docker cp resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/output1/ shared-folder/output/
    ```
3. Commit and push to your repo so that we can able to see your output


---

## Challenges and Solutions
The most challenging problem I faced was that my data was returning in the following format:
`Document3, Document1 Similarity, Document2, Document1 Similarity Similarity     0.0`

This is clearly wrong. I reviewed my logic for my mapper and reducer, only to find the problem was this line found within the controller:
`job.setCombinerClass(DocumentSimilarityReducer.class);`

I had blindly copied and modified these instructions from Hands On 4, which uses a combiner. The combiner is not useful for this, and ruined the output. After removing this, my output was correct.

Another challenge I faced was `Exception in thread "main" java.lang.ClassNotFoundException: com.example.control
ler.Controller`.

This issue was caused by the file structure being `src/main/com/example/...` which is not recognized by Maven.

To fix this, I changed the file structure to be `src/main/java/com/example/...`.

---
## Sample Input

**Input from `small_dataset.txt`**
```
Document1 This is a sample document containing words
Document2 Another document that also has words
Document3 Sample text with different words
```
## Sample Output

**Output from `small_dataset.txt`**
```
"Document1, Document2 Similarity: 0.56"
"Document1, Document3 Similarity: 0.42"
"Document2, Document3 Similarity: 0.50"
```
## Obtained Output: (Place your obtained output here.)

```
"Document1, Document2 Similarity:	0.02"
"Document1, Document3 Similarity:	0.18"
"Document2, Document3 Similarity:	0.16"
```
