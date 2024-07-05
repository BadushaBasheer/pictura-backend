package com.pictura_backend.controllers;

import com.pictura_backend.dto.LoginDTO;
import com.pictura_backend.dto.RegisterDTO;
import com.pictura_backend.dto.UserDTO;
import com.pictura_backend.response.LoginResponse;
import com.pictura_backend.services.auth.AuthService;
import com.pictura_backend.services.jwt.UserDetailsServiceImpl;
import com.pictura_backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final JwtUtil jwtUtil;

    private final AuthService authService;

    private final UserDetailsServiceImpl userDetailsService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> createAuthenticationToken(@RequestBody LoginDTO loginDTO, HttpServletResponse response) throws BadCredentialsException, DisabledException, UsernameNotFoundException, IOException, IOException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password!");
        } catch (DisabledException disabledException) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User is not activated");
            logger.info("User is not activated");
            return null;
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());

        final String jwtToken = jwtUtil.generateToken(userDetails.getUsername());

        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtUtil.getExpirationTime());
        return ResponseEntity.ok(loginResponse);

    }

    @PostMapping("/register")
    public ResponseEntity<Object> signupUser(@RequestBody RegisterDTO registerDTO) {
        UserDTO createdUser = authService.createUser(registerDTO);
        if (createdUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not created, please try again later!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
