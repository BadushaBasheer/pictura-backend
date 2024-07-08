package com.pictura_backend.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum BusinessErrorCodes {

    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, NOT_IMPLEMENTED, "The new password does not match"),
    ACCOUNT_LOCKED(302, FORBIDDEN, "User account is blocked"),
    ACCOUNT_DISABLED(302, FORBIDDEN, "User account is disabled"),
    BAD_CREDENTIALS(304, FORBIDDEN, "Login and / or password is incorrect")
    ;

    private final int code;

    private final String description;

    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
