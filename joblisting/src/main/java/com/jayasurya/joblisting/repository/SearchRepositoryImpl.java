package com.jayasurya.joblisting.repository;

import com.jayasurya.joblisting.model.Post;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Component
public class SearchRepositoryImpl implements SearchRepository {

    @Autowired
    MongoClient client;

    @Autowired
    MongoConverter converter;

    private static final Logger logger = Logger.getLogger(SearchRepositoryImpl.class.getName());

    @Override
    public List<Post> findByText(String text) {
        List<Post> posts = new ArrayList<>();

        // Get MongoDB Database and Collection
        MongoDatabase database = client.getDatabase("spring-boot-project-1");
        MongoCollection<Document> collection = database.getCollection("JobPost");

        // Debug: Check if collection exists
        if (collection.countDocuments() == 0) {
            logger.warning("⚠️ No documents found in JobPost collection!");
            return posts;
        }

        // Aggregation pipeline for full-text search
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
                new Document("$search",
                        new Document("text",
                                new Document("query", text)
                                        .append("path", Arrays.asList("techs", "desc", "profile")))),
                new Document("$sort", new Document("exp", 1)),  // Sorting by experience
                new Document("$limit", 5)  // Limit to 5 results
        ));

        // Convert results to Java objects
        result.forEach(doc -> {
            Post post = converter.read(Post.class, doc);
            if (post != null) {
                posts.add(post);
                logger.info("✅ Found match: " + post.getProfile());
            }
        });

        if (posts.isEmpty()) {
            logger.warning("⚠️ No matching job posts found for: " + text);
        }

        return posts;
    }
}
