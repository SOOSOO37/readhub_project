package com.readhub.userservice.security.service;

import com.readhub.global.exception.BusinessLogicException;
import com.readhub.global.exception.ExceptionCode;
import com.readhub.global.security.userdetail.CustomUserDetails;
import com.readhub.userservice.security.dto.Token;
import com.readhub.userservice.security.dto.UserProfile;
import com.readhub.global.security.jwt.JwtTokenProvider;
import com.readhub.global.security.redis.entity.LogoutAccessToken;
import com.readhub.global.security.redis.entity.RefreshToken;
import com.readhub.global.security.redis.repository.LogoutAccessTokenRedisRepository;
import com.readhub.global.security.redis.repository.RefreshTokenRepository;
import com.readhub.userservice.security.utils.OauthProvider;
import com.readhub.userservice.user.entity.User;
import com.readhub.userservice.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OauthService {
    private final InMemoryClientRegistrationRepository inMemoryRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final DefaultOAuth2UserService defaultOAuth2UserService;
    private final RestTemplate restTemplate;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public Token login(OauthProvider provider, String code) {
        log.info("🔍 Received OAuth Code: {}", code);

        String registrationId = provider.getDescription();
        ClientRegistration clientRegistration = inMemoryRepository.findByRegistrationId(registrationId);

        String token = getToken(code, clientRegistration);
        OAuth2User oAuth2User = getOAuth2User(token, clientRegistration);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        UserProfile userProfile = OauthProvider.extract(registrationId, attributes);

        User user = getOrSaveUser(userProfile);
        return createToken(user);
    }

    public String getToken(String code, ClientRegistration clientRegistration) {
        String uri = clientRegistration.getProviderDetails().getTokenUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(tokenRequest(code, clientRegistration), headers);

        try {
            ResponseEntity<Map<String, String>> responseEntity = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );
            log.info("🔍 OAuth Token Response: {}", responseEntity.getBody());

            if (responseEntity.getBody() == null || !responseEntity.getBody().containsKey("access_token")) {
                log.error("OAuth token response is null or missing access_token: {}", responseEntity.getBody());
                throw new BusinessLogicException(ExceptionCode.PERMISSION_NOT_EXIST);
            }

            return responseEntity.getBody().get("access_token");
        } catch (HttpClientErrorException.BadRequest e) {
            log.error("OAuth token request failed: {}", e.getMessage());
            throw new BusinessLogicException(ExceptionCode.PERMISSION_NOT_EXIST);
        }
    }

    private OAuth2User getOAuth2User(String token, ClientRegistration clientRegistration) {
        OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse.withToken(token)
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(3600L)
                .build();

        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, tokenResponse.getAccessToken());
        return defaultOAuth2UserService.loadUser(userRequest);
    }

    private MultiValueMap<String, String> tokenRequest(String code, ClientRegistration provider) {
        log.info("🔍 Client ID: {}, Client Secret: {}", provider.getClientId(), provider.getClientSecret());
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", provider.getRedirectUri());
        formData.add("client_id", provider.getClientId());
        return formData;
    }

    private User getOrSaveUser(UserProfile userProfile) {
        User user = getUser(userProfile);
        return (user == null) ? saveUser(userProfile) : user;
    }

    private User getUser(UserProfile userProfile) {
        return userRepository.findByEmail(userProfile.getEmail()).orElse(null);
    }

    private User saveUser(UserProfile userProfile) {
        if (userProfile.getEmail() == null || !userProfile.getEmail().contains("@")) {
            log.error("Invalid email format: {}", userProfile.getEmail());
            throw new IllegalArgumentException("Invalid email format.");
        }

        User user = User.builder()
                .email(userProfile.getEmail())
                .password("oauthUser")
                .nickName(userProfile.getEmail().split("@")[0])
                .gender(userProfile.getGender())
                .build();

        return userRepository.save(user);
    }

    private Token createToken(User user) {
        if (jwtTokenProvider.getSecretKey() == null) {
            log.error("JWT secret key is not configured.");
            throw new IllegalStateException("JWT secret key is not configured.");
        }

//        CustomUserDetails userDetails = new CustomUserDetails(
//                user.getId(),
//                user.getEmail(),
//                user.getPassword(),
//                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
//        );
        CustomUserDetails userDetails = new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDetails.getId());
        claims.put("authorities", authentication.getAuthorities());
        claims.put("roles", user.getRoles());

//        String accessToken = jwtTokenProvider.generateAccessToken(
////                claims,
////                userDetails.getUsername(),
////                jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getAccessTokenExpirationMinutes()),
////                jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey())
////        );
        String accessToken = jwtTokenProvider.generateAccessToken(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                userDetails.getUsername(),
                jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getRefreshTokenExpirationMinutes()),
                jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey())
        );

        return new Token(accessToken, refreshToken, user.getId());
    }
    //로그아웃
//    public Map<String, String> reissue(String email, String refreshToken) {
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
//
//        RefreshToken redisRefreshToken = refreshTokenRepository.findById(email)
//                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.TOKEN_NOT_FOUND));
//
//        if (refreshToken.equals(redisRefreshToken.getRefreshToken())) {
//            Map<String, String> token = new HashMap<>();
//            token.put("accessToken", jwtTokenProvider.generateAccessToken(user));
//            token.put("refreshToken", jwtTokenProvider.generateRefreshToken(user.getEmail()));
//
//            redisRefreshToken.setRefreshToken(token.get("refreshToken"));
//            refreshTokenRepository.save(redisRefreshToken);
//
//            return token;
//        } else {
//            throw new IllegalArgumentException("토큰이 일치하지 않습니다.");
//        }
//    }
//    public Map<String, String> reissue(String email, String refreshToken) {
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
//
//        RefreshToken redisRefreshToken = refreshTokenRepository.findById(email)
//                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.TOKEN_NOT_FOUND));
//
//        if (refreshToken.equals(redisRefreshToken.getRefreshToken())) {
//            // ✅ 1. claim 구성
//            Map<String, Object> claims = new HashMap<>();
//            claims.put("username", user.getEmail());
//            claims.put("roles", user.getRoles());
//
//            String base64SecretKey = jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey());
//
//            // ✅ 2. accessToken/refreshToken 발급
//            String accessToken = jwtTokenProvider.generateAccessToken(
//                    claims,
//                    user.getEmail(),
//                    jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getAccessTokenExpirationMinutes()),
//                    base64SecretKey
//            );
//
//            String newRefreshToken = jwtTokenProvider.generateRefreshToken(
//                    user.getEmail(),
//                    jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getRefreshTokenExpirationMinutes()),
//                    base64SecretKey
//            );
//
//            // ✅ 3. Redis 저장 및 반환
//            redisRefreshToken.setRefreshToken(newRefreshToken);
//            refreshTokenRepository.save(redisRefreshToken);
//
//            Map<String, String> token = new HashMap<>();
//            token.put("accessToken", accessToken);
//            token.put("refreshToken", newRefreshToken);
//            return token;
//
//        } else {
//            throw new IllegalArgumentException("토큰이 일치하지 않습니다.");
//        }
//    }
    public Map<String, String> reissue(String email, String refreshToken) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        RefreshToken redisRefreshToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.TOKEN_NOT_FOUND));

        if (refreshToken.equals(redisRefreshToken.getRefreshToken())) {

            // ✅ accessToken에 id, email, roles 포함
            String accessToken = jwtTokenProvider.generateAccessToken(
                    user.getId(),
                    user.getEmail(),
                    user.getRoles()
            );

            // ✅ refreshToken 발급
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

            // ✅ Redis에 갱신
            redisRefreshToken.setRefreshToken(newRefreshToken);
            refreshTokenRepository.save(redisRefreshToken);

            // ✅ 반환
            Map<String, String> token = new HashMap<>();
            token.put("accessToken", accessToken);
            token.put("refreshToken", newRefreshToken);
            return token;

        } else {
            throw new IllegalArgumentException("토큰이 일치하지 않습니다.");
        }
    }


    public void logout(User user, String accessToken) {
        String email = user.getEmail();
        String jws = accessToken.replace("Bearer ", "");
        Long expiration = jwtTokenProvider.getExpiration(jws);

        LogoutAccessToken logoutAccessToken = LogoutAccessToken.of(jws, email, expiration);
        logoutAccessTokenRedisRepository.save(logoutAccessToken);

        deleteRefreshToken(email);
    }

    private void deleteRefreshToken(String email) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findById(email);

        if (optionalRefreshToken.isPresent()) {
            refreshTokenRepository.deleteById(email);
        }
    }

}
