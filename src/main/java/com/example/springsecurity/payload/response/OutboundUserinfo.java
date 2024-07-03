package com.example.springsecurity.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OutboundUserinfo {
    String id;
    String name;
    String givenName;
    String familyName;
    String picture;
    String locale;
    String email;
    boolean verifiedEmail;
}
