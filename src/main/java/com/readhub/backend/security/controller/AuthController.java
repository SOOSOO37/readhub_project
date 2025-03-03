package com.readhub.backend.security.controller;

import com.readhub.backend.security.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/kakao")
public class AuthController {

    private final OAuthService oAuthService;

    @GetMapping("/redirect")
    public ResponseEntity<Map<String, String>> kakaoLogin(@RequestParam String code) {
        Map<String, String> tokens = oAuthService.loginKakao(code);
        return ResponseEntity.ok(tokens);
    }
}
