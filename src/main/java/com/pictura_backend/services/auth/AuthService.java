package com.pictura_backend.services.auth;

import com.pictura_backend.dto.RegisterDTO;
import com.pictura_backend.dto.UserDTO;

public interface AuthService {

    UserDTO registerUser(RegisterDTO registerDTO);
}
