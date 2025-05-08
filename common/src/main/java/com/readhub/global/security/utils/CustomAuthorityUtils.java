package com.readhub.global.security.utils;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthorityUtils {

    @Setter
    @Value("${config.admin}")
    private String adminEmail;

    private final List<String> ADMIN_ROLES_STRING = List.of("ADMIN");

    private final List<String> USER_ROLES_STRING = List.of("USER");


    private final List<GrantedAuthority> ADMIN_ROLES
            = AuthorityUtils.createAuthorityList("ROLE_ADMIN");

    private final List<GrantedAuthority> USER_ROLES =
            AuthorityUtils.createAuthorityList("ROLE_USER");

    public boolean isAdminEmail(String email) {
        return adminEmail.contains(email);
    }

    public List<GrantedAuthority> createAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        return authorities;
    }

//    public List<String> createRoles(User user) {
//        if (user.getEmail().equals(adminEmail)) {
//            return ADMIN_ROLES_STRING;
//        }
//        return USER_ROLES_STRING;
//    }
    public List<String> createRoles(String email) {
        return isAdminEmail(email) ? ADMIN_ROLES_STRING : USER_ROLES_STRING;
    }

    public List<GrantedAuthority> createAuthorities(String email) {
        if (email.equals(adminEmail)) {
            return ADMIN_ROLES;
        }else
            return USER_ROLES;
    }
}

