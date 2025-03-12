package com.readhub.backend.security.controller;

import com.readhub.backend.security.dto.Oauth;
import com.readhub.backend.security.dto.Token;
import com.readhub.backend.security.service.OauthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/kakao")
public class AuthController {

    private final OauthService oAuthService;

    @PostMapping("/login")
    public ResponseEntity<Void> oauth(@RequestBody @Valid Oauth oAuth) {
        Token token = oAuthService.login(oAuth.getProvider(), oAuth.getCode());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token.getAccessToken());
        headers.add("Refresh", "Bearer " + token.getRefreshToken());
        headers.add("userId", String.valueOf(token.getId()));

        return ResponseEntity.ok().headers(headers).build();
    }

}
