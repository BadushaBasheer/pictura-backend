package com.pictura_backend.services.post;

import com.pictura_backend.entities.Post;

import java.util.List;

public interface PostService {

    List<Post> findAllPosts();

    List<Post> findPostByUserId(Long userId);

    Post findPostById(Long postId) throws Exception;

    Post createNewPost(Post post, Long userId) throws Exception;

    Post likePost(Long postId, Long userId) throws Exception;

    Post commentPost(Long postId, Long userId) throws Exception;

    Post savedPost(Long postId, Long userId) throws Exception;

    String deletePost(Long postId, Long userId) throws Exception;
}
