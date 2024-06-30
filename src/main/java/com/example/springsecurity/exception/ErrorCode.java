package com.example.springsecurity.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;


@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    ROLE_FOUND(1003, "ROLE NOT FOUND", HttpStatus.NOT_FOUND),
    USER_EXISTED(1001,"USER existed", HttpStatus.BAD_REQUEST),
    INVALID_USER(1002,"USER MUST BE AT LEAST 3 CHARACTER", HttpStatus.BAD_REQUEST)
    ;
    int code;
    String message;
    HttpStatus status;
    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}
