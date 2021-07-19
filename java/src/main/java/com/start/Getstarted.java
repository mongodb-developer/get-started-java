package com.start;

import com.mongodb.Block;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.result.DeleteResult;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import static java.util.Arrays.asList;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Sorts.descending;

public class Getstarted {
    public static void main(final String[] args) {
        String mongoURI = System.getenv("MONGODB_URI");

        MongoClient mongoClient = MongoClients.create(mongoURI);

        MongoDatabase database = mongoClient.getDatabase("getstarted");
        MongoCollection<Document> collection = database.getCollection("java");

        System.out.println("Connecting to cluster");
        collection.drop();
        System.out.println("Collection [getstarted.java] has been reset");

        // make a document and insert it
        Document doc = new Document("name", "MongoDB")
                       .append("type", "database")
                       .append("count", 1)
                       .append("info", new Document("x", 203).append("y", 102));
        System.out.println("Inserting one document");
        collection.insertOne(doc);
        Document myDoc = collection.find().first();
        System.out.println("Querying one document:");
        System.out.println("\t" + myDoc.toJson());

        // insert many
        List<Document> documents = new ArrayList<Document>();
        for (int i = 0; i < 5; i++) {
            documents.add(new Document("i", i));
        }
        System.out.println("Inserting many documents:");
        collection.insertMany(documents);
        System.out.println("\tTotal # of documents: " + collection.countDocuments());

        // lets get all the documents in the collection and print them out
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }

        // now use a query to get 1 document out
        myDoc = collection.find(eq("i", 3)).first();
        System.out.println("Query one document:");
        System.out.println("\t" + myDoc.toJson());

        // Sorting
        myDoc = collection.find(exists("i")).sort(descending("i")).first();
        System.out.println("Query one sorted document:");
        System.out.println("\t" + myDoc.toJson());
        
        // Projection
        myDoc = collection.find().projection(excludeId()).first();
        System.out.println("Query one document excluding its ID:");
        System.out.println("\t" + myDoc.toJson());

        // Update a document
        UpdateResult upResult = collection.updateOne(eq("i", 3), new Document("$set", new Document("x", 20)));
        System.out.println("Updated a document:");
        System.out.println("\t" + upResult.getModifiedCount());

        // Delete a document
        DeleteResult delResult = collection.deleteOne(eq("i", 3));
        System.out.println("Deleted a document:");
        System.out.println("\t" + delResult.getDeletedCount()); 

        // Aggregation
        Document group = Document.parse("{$group:{_id: null, total :{$sum:'$i'}}}"); 
        List<Document> pipeline = asList(group);
        
        Consumer<Document> printBlock = new Consumer<Document>() {
            public void accept(final Document doc) {
                System.out.println(doc.toJson());
            };
        };     
        AggregateIterable<Document> iterable = collection.aggregate(pipeline);
        System.out.println("Aggregation Result:");
        iterable.forEach(printBlock); 

        // release resources
        mongoClient.close();
    }
}