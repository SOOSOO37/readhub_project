package com.readhub.backend.security.service;

import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.security.dto.Token;
import com.readhub.backend.security.dto.UserProfile;
import com.readhub.backend.security.jwt.JwtTokenProvider;
import com.readhub.backend.security.userdetail.CustomUserDetails;
import com.readhub.backend.security.userdetail.CustomUserDetailsService;
import com.readhub.backend.security.utils.OauthProvider;
import com.readhub.backend.user.entity.User;
import com.readhub.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OauthService {
    private final InMemoryClientRegistrationRepository inMemoryRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final DefaultOAuth2UserService defaultOAuth2UserService;
    private final RestTemplate restTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;


    @Transactional
    public Token login(OauthProvider provider, String code) {

        String registrationId = provider.getDescription();

        ClientRegistration clientRegistration = inMemoryRepository.findByRegistrationId(registrationId);

        String token = getToken(code, clientRegistration);

        OAuth2User oAuth2User = getOAuth2User(token, clientRegistration);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

        UserProfile userProfile = OauthProvider.extract(registrationId, attributes);

        User user = getOrSaveUser(userProfile);

        Token jwtToken = createToken(user);

        return jwtToken;
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
                    new ParameterizedTypeReference<>(){}
            );

            return responseEntity.getBody().get("access_token");
        } catch (HttpClientErrorException.BadRequest e) {
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
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", provider.getRedirectUri());
        formData.add("client_id", provider.getClientId());
        return formData;
    }

    private User getOrSaveUser(UserProfile userProfile) {
        User user = getUser(userProfile);

        if (user == null) {
            user = saveUser(userProfile);
        }
        return user;
    }

    private User getUser(UserProfile memberProfile) {
        return userRepository.findByEmail(memberProfile.getEmail())
                .orElse(null);
    }

    private User saveUser(UserProfile userProfile) {
        User user = User.builder()
                .email(userProfile.getEmail())
                .password("oauthUser")
                .nickName(userProfile.getEmail().split("@")[0])
                .gender(userProfile.getGender())
                .build();

        User signUser = userRepository.save(user);
        return signUser;
    }

    private Token createToken(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")
                ));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDetails.getId());
        claims.put("authorities", authentication.getAuthorities());
        claims.put("roles", user.getRoles());

        String accessToken = jwtTokenProvider.generateAccessToken(
                claims,
                userDetails.getUsername(),
                jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getAccessTokenExpirationMinutes()),
                jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey()) // 기본 키 사용
        );
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                userDetails.getUsername(),
                jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getRefreshTokenExpirationMinutes()),
                jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey())
        );

        return new Token(accessToken, refreshToken, user.getId());
    }

}

