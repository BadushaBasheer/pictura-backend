package com.pictura_backend.services.auth;

import com.pictura_backend.dto.RegisterDTO;
import com.pictura_backend.dto.UserDTO;
import com.pictura_backend.entities.Role;
import com.pictura_backend.entities.User;
import com.pictura_backend.repositories.RoleRepository;
import com.pictura_backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserDTO createUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        Role userRole = roleRepository.findByName("ROLE_USER");  // Fetch the "ROLE_USER" role from the database
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
            logger.info("Role 'ROLE_USER' created and assigned.");
        }

        user.getRoles().add(userRole); // Assign the "ROLE_USER" role to the new user
        User createdUser = userRepository.save(user);

        // Convert to UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setId(createdUser.getId());
        userDTO.setEmail(createdUser.getEmail());
        userDTO.setName(createdUser.getUsername());

        logger.info("User created successfully: {}", userDTO.getEmail());

        return userDTO;
    }

}
