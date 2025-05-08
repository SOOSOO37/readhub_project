package com.readhub.bookservice.security;

import com.readhub.global.security.jwt.JwtTokenProvider;
import com.readhub.global.security.filter.JwtVerificationFilter;
import com.readhub.global.security.utils.CustomAuthorityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthorityUtils authorityUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/rents/**").permitAll()
                        .requestMatchers("/books/**").permitAll()
                        .requestMatchers("/book-page/**").permitAll()
                        .requestMatchers("/categories/**").permitAll()
                        .requestMatchers("/favorite/**").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterAfter(new JwtVerificationFilter(jwtTokenProvider, authorityUtils),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
