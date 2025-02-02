package com.jayasurya.joblisting.repository;

import com.jayasurya.joblisting.model.Post;

import java.util.List;

public interface SearchRepository {
    List<Post> findByText(String text);
}
