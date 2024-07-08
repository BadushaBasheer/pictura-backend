package com.pictura_backend.repositories;

import com.pictura_backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByEmail(String email);

    Optional<User> findByEmail(String email);

    User findUserById(Long userId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String mail);

    List<User> findByUsernameContainingOrEmailContaining(String username, String Email);


}
