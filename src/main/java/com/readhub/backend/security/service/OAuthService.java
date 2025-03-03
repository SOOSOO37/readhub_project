package com.readhub.backend.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readhub.backend.security.dto.KakaoUserInfo;
import com.readhub.backend.security.jwt.JwtTokenProvider;
import com.readhub.backend.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class OAuthService {

    //@Value("${kakao.client-id}")
    private String clientId;

   //@Value("${kakao.redirect-uri}")
    private String redirectUri;

    //@Value("${kakao.token-uri}")
    private String tokenUri;

    //@Value("${kakao.user-info-uri}")
    private String userInfoUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final JwtTokenProvider jwtTokenProvider;

    public String getAccessToken(String authorizationCode) {
        String requestUrl = UriComponentsBuilder.fromHttpUrl(tokenUri)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("code", authorizationCode)
                .toUriString();

        ResponseEntity<Map> response = restTemplate.postForEntity(requestUrl, null, Map.class);
        return response.getBody() != null ? (String) response.getBody().get("access_token") : null;
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, Map.class);

        Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return KakaoUserInfo.builder()
                .email((String) kakaoAccount.get("email"))
                .nickname((String) profile.get("nickname"))
                .profileImage((String) profile.get("profile_image_url"))
                .build();
    }

    public Map<String, String> loginKakao(String authorizationCode) {

        String accessToken = getAccessToken(authorizationCode);

        KakaoUserInfo kakaoUserInfo = getUserInfo(accessToken);

        String base64SecretKey = jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey());
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", kakaoUserInfo.getEmail());

        String jwtAccessToken = jwtTokenProvider.generateAccessToken(claims, kakaoUserInfo.getEmail(),
                jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getAccessTokenExpirationMinutes()), base64SecretKey);

        String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(kakaoUserInfo.getEmail());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", jwtAccessToken);
        tokens.put("refreshToken", jwtRefreshToken);

        return tokens;
    }

}

