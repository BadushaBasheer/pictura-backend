package com.pictura_backend.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "admin")
public class AdminProperties {

    private String email;

    private String password;
}
