package com.pictura_backend.configuration;

import com.pictura_backend.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class GoogleOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final WebClient userInfoClient;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        User userInfo = userInfoClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth2/v3/userinfo")
                        .queryParam("access_token", token)
                        .build())
                .retrieve()
                .bodyToMono(User.class)
                .block();

        if (userInfo == null) {
            throw new IllegalArgumentException("Invalid token: User info could not be retrieved");
        }

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", userInfo.getId());
        attributes.put("username", userInfo.getUsername());
        attributes.put("email", userInfo.getEmail());

        return new OAuth2IntrospectionAuthenticatedPrincipal(userInfo.getUsername(), attributes, null);
    }

}

