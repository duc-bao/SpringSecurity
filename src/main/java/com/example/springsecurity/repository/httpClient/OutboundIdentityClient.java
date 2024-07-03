package com.example.springsecurity.repository.httpClient;

import com.example.springsecurity.payload.request.ExchangeTokenRequest;
import com.example.springsecurity.payload.response.ExchangeTokenResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
// Chuyển hướng đến trang này
@FeignClient(name = "Web-client-1", url = "https://oauth2.googleapis.com")
public interface OutboundIdentityClient {
    @PostMapping(value = "/token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest request);
}
