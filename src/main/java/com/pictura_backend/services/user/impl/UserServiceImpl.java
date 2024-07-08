package com.pictura_backend.services.user.impl;


import com.pictura_backend.entities.Follower;
import com.pictura_backend.entities.Following;
import com.pictura_backend.entities.User;
import com.pictura_backend.repositories.UserRepository;
import com.pictura_backend.services.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

//-----------------------------------COMMON---------------------------------------------

    @Override
    public User findUserById(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user != null) {
            return user;
        }
        throw new UsernameNotFoundException("User not found with ID: " + userId);
    }

//---------------------------------GET ALL USER--------------------------------------------

    @Override
    public List<User> allUsers() {
        return new ArrayList<>(userRepository.findAll());
    }


//---------------------------------SEARCH USER--------------------------------------------

    @Override
    public List<User> searchUser(String query) {
        return userRepository.findByUsernameContainingOrEmailContaining(query, query);
    }

    //---------------------------------UPDATE USER--------------------------------------------
    @Override
    public User updateUser(User user) {
        Optional<User> userUpdateOpt = userRepository.findByEmail(user.getEmail());
        return userUpdateOpt
                .map(existingUser -> updateExistingUser(user, existingUser))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private User updateExistingUser(User user, User existingUser) {
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(existingUser);
    }

//---------------------------------DELETE USER--------------------------------------------

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with the id: " + userId);
        }
        else {
            userRepository.deleteById(userId);
        }
    }

    //---------------------------------FOLLOW USER--------------------------------------------
    @Override
    public User followUser(Long userId1, Long userId2) {
        User user1 = findUserById(userId1);
        User user2 = findUserById(userId2);

        Following following = new Following();
        following.setUser(user1);
        following.setFollowingId(user2.getId());

        Follower follower = new Follower();
        follower.setUser(user2);
        follower.setFollowerId(user1.getId());

        user1.getFollowing().add(following);
        user2.getFollowers().add(follower);

        userRepository.save(user1);
        userRepository.save(user2);

        return user1;
    }

}


