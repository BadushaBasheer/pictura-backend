package com.pictura_backend.services.auth;

import com.pictura_backend.dto.LoginDTO;
import com.pictura_backend.dto.RegisterDTO;
import com.pictura_backend.dto.UserDTO;
import com.pictura_backend.response.LoginResponse;
import jakarta.mail.MessagingException;

public interface AuthService {

    UserDTO registerUser(RegisterDTO registerDTO) throws MessagingException;

    void activateAccount(String token) throws MessagingException;

    LoginResponse authenticate(LoginDTO loginDTO);
}
