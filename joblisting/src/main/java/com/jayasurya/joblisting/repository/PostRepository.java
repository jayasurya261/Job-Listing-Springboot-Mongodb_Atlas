package com.jayasurya.joblisting.repository;

import com.jayasurya.joblisting.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post,String> {
}
