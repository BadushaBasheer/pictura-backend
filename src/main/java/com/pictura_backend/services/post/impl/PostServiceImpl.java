package com.pictura_backend.services.post.impl;

import com.pictura_backend.entities.Post;
import com.pictura_backend.entities.User;
import com.pictura_backend.repositories.PostRepository;
import com.pictura_backend.services.post.PostService;
import com.pictura_backend.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final UserService userService;

    private final PostRepository postRepository;

    @Override
    public List<Post> findAllPosts() {
        try {
            return postRepository.findAll();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch all posts", ex);
        }
    }

    @Override
    public Post findPostById(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new NoSuchElementException("Post not found with id : " + postId);
        }
        return post.get();
    }

    @Override
    public List<Post> findPostByUserId(Long userId) {
        return postRepository.findPostByUserId(userId);
    }

    @Override
    public Post createNewPost(Post post, Long userId) {
        User user = userService.findUserById(userId);
        if (user != null) {
            Post post1 = new Post();
            post1.setUser(user);
            post1.setImage(post.getImage());
            post1.setVideo(post.getVideo());
            post1.setCaption(post.getCaption());
            post1.setCreatedAt(LocalDateTime.now());
            return postRepository.save(post1);
        }
        else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    @Override
    public Post savedPost(Long postId, Long userId) {
        Post post = findPostById(postId);
        User user = userService.findUserById(userId);
        if (post == null || user == null) {
            return null;
        }
        List<Post> savedPosts = user.getSavedPost();

        if (savedPosts.contains(post)) {
            savedPosts.remove(post);
        }
        else {
            savedPosts.add(post);
        }
        user.setSavedPost(savedPosts);
        userService.updateUser(user);
        return post;
    }

    @Override
    public Post likePost(Long postId, Long userId) {
        Post post = findPostById(postId);
        User user = userService.findUserById(userId);
        List<User> likedUsers = post.getLiked();
        if (likedUsers == null) {
            likedUsers = new ArrayList<>();
        }
        if (likedUsers.contains(user)) {
            likedUsers.remove(user);
        }
        else {
            likedUsers.add(user);
        }
        post.setLiked(likedUsers);
        return postRepository.save(post);
    }

    @Override
    public Post commentPost(Long postId, Long userId) throws Exception {
        Post post = findPostById(postId);
        User user = userService.findUserById(userId);

        return null;
    }

    @Override
    public String deletePost(Long postId, Long userId) {
        Post post = findPostById(postId);
        User user = userService.findUserById(userId);
        if (!Objects.equals(post.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException("You can't delete another user's post");
        }
        postRepository.deleteById(postId);
        return "Post with ID " + postId + " has been deleted successfully";
    }

}

