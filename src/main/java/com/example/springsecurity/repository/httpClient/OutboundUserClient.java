package com.example.springsecurity.repository.httpClient;

import com.example.springsecurity.payload.response.OutboundUserinfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "web-client-info-user", url = "https://www.googleapis.com")
public interface OutboundUserClient {
    @GetMapping(value = "/oauth2/v1/userinfo")
    OutboundUserinfo getUserInfo(@RequestParam("alt") String alt, @RequestParam("access_token") String accessToken);
}
