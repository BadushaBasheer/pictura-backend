package com.pictura_backend.services.user;

import com.pictura_backend.entities.User;

import java.util.List;

public interface UserService {

    List<User> allUsers();

    User findUserById(Long userId);

    List<User> searchUser(String query);

    User updateUser(User user);

    void deleteUser(Long userId);

    User followUser(Long userId1, Long userId2);
}
