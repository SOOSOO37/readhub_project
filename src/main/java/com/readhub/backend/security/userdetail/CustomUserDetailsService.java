package com.readhub.backend.security.userdetail;

import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.security.utils.CustomAuthorityUtils;
import com.readhub.backend.user.entity.User;
import com.readhub.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CustomAuthorityUtils authorityUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> optionalUser = userRepository.findByEmail(username);
        User user = optionalUser.orElse(null);

        if (user != null && user.getUserStatus() != User.UserStatus.QUIT) {
            return new CustomUserDetails(user);
        }
        throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
    }

    private final class CustomUserDetails extends User implements UserDetails {

        CustomUserDetails(User user) {
            setId(user.getId());
            setEmail(user.getEmail());
            setPassword(user.getPassword());
            setNickName(user.getNickName());
            setRoles(user.getRoles());
            setUserStatus(user.getUserStatus());
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorityUtils.createAuthorities(this.getRoles());
        }

        @Override
        public String getUsername() {
            return getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
