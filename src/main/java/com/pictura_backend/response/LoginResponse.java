package com.pictura_backend.response;

import lombok.Builder;

@Builder
public record LoginResponse(String token, long expiresIn) {
}

