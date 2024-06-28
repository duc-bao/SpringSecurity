package com.example.springsecurity.payload.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MessageResponse {
    String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}
