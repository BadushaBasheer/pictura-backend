package com.pictura_backend.services.auth;

import com.pictura_backend.dto.LoginDTO;
import com.pictura_backend.dto.RegisterDTO;
import com.pictura_backend.dto.UserDTO;
import com.pictura_backend.util.EmailTemplateName;
import com.pictura_backend.entities.Role;
import com.pictura_backend.entities.Token;
import com.pictura_backend.entities.User;
import com.pictura_backend.repositories.RoleRepository;
import com.pictura_backend.repositories.TokenRepository;
import com.pictura_backend.repositories.UserRepository;
import com.pictura_backend.response.LoginResponse;
import com.pictura_backend.services.jwt.JwtService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final EmailService emailService;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenRepository tokenRepository;

    private final AuthenticationManager authenticationManager;

    @Value("${mailing.frontend.activation-url}")
    private String activationUrl;

    @Transactional
    @Override
    public UserDTO registerUser(RegisterDTO registerDTO) throws MessagingException {
        User user = new User();
        user.setUsername(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setCreatedDate(LocalDateTime.now());
        user.setAccountLocked(false);
        user.setEnabled(false);

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

        sendValidationEmail(user);

        logger.info("User created successfully: {}", userDTO.getEmail());

        return userDTO;
    }

//    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    @Override
    public LoginResponse authenticate(LoginDTO loginDTO) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();
        var user = ((User)auth.getPrincipal());
        claims.put("username", user.getUsername());
        var jwtToken = jwtService.generateToken(claims, user);
        return LoginResponse.builder().token(jwtToken).build();
    }


    private String generateAndSaveActivationToken(User user) {
        // Generate a token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getUsername(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
    //----------------------------------------



}
