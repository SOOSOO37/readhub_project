package com.readhub.backend.security.controller;

import com.readhub.backend.security.dto.Oauth;
import com.readhub.backend.security.dto.Token;
import com.readhub.backend.security.service.OauthService;
import com.readhub.backend.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final OauthService oAuthService;

    @PostMapping("/oauth")
    public ResponseEntity<Void> oauth(@RequestBody @Valid Oauth oAuth) {
        Token token = oAuthService.login(oAuth.getProvider(), oAuth.getCode());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token.getAccessToken());
        headers.add("Refresh", "Bearer " + token.getRefreshToken());
        headers.add("userId", String.valueOf(token.getId()));

        return ResponseEntity.ok().headers(headers).build();
    }

    @GetMapping("/reissue")
    public ResponseEntity reissue(@RequestHeader("RefreshToken") String refreshToken,
                                  @RequestHeader("email") String email,
                                  HttpServletResponse response) {
        Map<String, String> token = oAuthService.reissue(email, refreshToken);

        response.setHeader("Authorization", "Bearer " + token.get("accessToken"));
        response.setHeader("Refresh", token.get("refreshToken"));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/logout")
    public ResponseEntity logout(@AuthenticationPrincipal User user,
                                 @RequestHeader("Authorization") String accessToken) {
        oAuthService.logout(user, accessToken);
        log.info("Logout : " + user.getEmail());

        return ResponseEntity.ok().build();
    }

}
