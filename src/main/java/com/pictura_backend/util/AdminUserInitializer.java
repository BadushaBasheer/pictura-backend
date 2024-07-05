package com.pictura_backend.util;

import com.pictura_backend.entities.Role;
import com.pictura_backend.entities.User;
import com.pictura_backend.repositories.RoleRepository;
import com.pictura_backend.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserInitializer.class);

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AdminProperties adminProperties;

    @PostConstruct
    public void initializeAdminUser() {
        String adminEmail = adminProperties.getEmail();
        String adminPassword = adminProperties.getPassword();
        if (!userRepository.existsByEmail(adminEmail)) {
            User adminUser = new User();
            adminUser.setUsername("Admin");
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            logger.info("Admin created successfully {}", adminEmail);

            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleRepository.save(adminRole);
            }

            adminUser.getRoles().add(adminRole);

            userRepository.save(adminUser);
        } else {
            logger.info("Admin already present in the database {}", adminEmail);
        }
    }
}

