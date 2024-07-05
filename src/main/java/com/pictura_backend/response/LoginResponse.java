package com.pictura_backend.response;

public record LoginResponse(String token, long expiresIn) {
}

