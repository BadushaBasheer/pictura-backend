package com.pictura_backend.controllers;

import com.pictura_backend.entities.Post;
import com.pictura_backend.response.ApiResponse;
import com.pictura_backend.services.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> findAllPost() {
        try {
            List<Post> posts = postService.findAllPosts();
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/posts/user/{userId}")
    public ResponseEntity<Post> createPost(@RequestBody Post post, @PathVariable Long userId) throws Exception {
        Post createdPost = postService.createNewPost(post, userId);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/posts/user/{userId}")
    public ResponseEntity<List<Post>> findUsersPost(@PathVariable Long userId) {
        List<Post> posts = postService.findPostByUserId(userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> findPostByIdHandler(@PathVariable Long postId) throws Exception {
        Post post = postService.findPostById(postId);
        return new ResponseEntity<>(post, HttpStatus.ACCEPTED);
    }

    @PutMapping("/posts/save/{postId}/user/{userId}")
    public ResponseEntity<Post> savePostByIdHandler(@PathVariable Long postId, @PathVariable Long userId) throws Exception {
        Post post = postService.savedPost(postId, userId);
        return new ResponseEntity<>(post, HttpStatus.ACCEPTED);
    }

    @PutMapping("/posts/like/{postId}/user/{userId}")
    public ResponseEntity<Post> likePostByIdHandler(@PathVariable Long postId, @PathVariable Long userId) throws Exception {
        Post post = postService.likePost(postId, userId);
        return new ResponseEntity<>(post, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/posts/{postId}/user/{userId}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long postId, @PathVariable Long userId) {
        try {
            String message = postService.deletePost(postId, userId);
            ApiResponse response = new ApiResponse(message, true);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            String errorMessage = "Failed to delete the post: " + ex.getMessage();
            ApiResponse errorResponse = new ApiResponse(errorMessage, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
