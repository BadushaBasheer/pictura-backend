package com.pictura_backend.controllers;

import com.pictura_backend.entities.User;
import com.pictura_backend.services.user.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/userDetail")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long  userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("/search")
    public List<User> searchUser(@RequestParam("query") String query) {
        return userService.searchUser(query);
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        updatedUser.setId(userId);
        return userService.updateUser(updatedUser);
    }
    @PutMapping("/follow/{userId1}/{userId2}")
    public ResponseEntity<User> followUserHandler(@PathVariable Long userId1, @PathVariable Long userId2) {
        try {
            User updatedUser = userService.followUser(userId1, userId2);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with the id: " + userId);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the user");
        }
    }
}
