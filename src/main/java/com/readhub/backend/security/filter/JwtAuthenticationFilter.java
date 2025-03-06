package com.readhub.backend.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readhub.backend.security.dto.LoginRequestDto;
import com.readhub.backend.security.jwt.JwtTokenProvider;
import com.readhub.backend.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequestDto loginDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Object principal = authResult.getPrincipal();

            User user = (User) authResult.getPrincipal();

            String accessToken = delegateAccessToken(user);
            String refreshToken = delegateRefreshToken(user);

            response.setHeader("Authorization", "Bearer " + accessToken);
            response.setHeader("Refresh", refreshToken);

            this.getSuccessHandler().onAuthenticationSuccess(request,response,authResult);
        }


    public String delegateAccessToken (User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getEmail());
        claims.put("roles", user.getRoles());

        String subject = user.getEmail();
        Date expiration = jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getAccessTokenExpirationMinutes());

        String base64SecretKey = jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey());
        String accessToken = jwtTokenProvider.generateAccessToken(claims,subject,expiration,base64SecretKey);

        return accessToken;
    }

    public String delegateRefreshToken(User user){
        String subject = user.getEmail();
        Date expiration = jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getRefreshTokenExpirationMinutes());
        String base64SecretKey = jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey());

        String refreshToken = jwtTokenProvider.generateRefreshToken(subject,expiration,base64SecretKey);

        return refreshToken;
    }
}
