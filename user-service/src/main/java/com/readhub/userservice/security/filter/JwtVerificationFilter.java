package com.readhub.userservice.security.filter;

import com.readhub.global.exception.BusinessLogicException;
import com.readhub.global.exception.ExceptionCode;
import com.readhub.global.security.utils.CustomAuthorityUtils;
import com.readhub.global.security.jwt.JwtTokenProvider;
import com.readhub.global.security.redis.entity.LogoutAccessToken;
import com.readhub.global.security.redis.repository.LogoutAccessTokenRedisRepository;
import com.readhub.global.security.utils.UserResignedException;
import com.readhub.userservice.security.userdetail.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final CustomAuthorityUtils authorityUtils;

    private final CustomUserDetailsService userDetailsService;

    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            Map<String,Object> claims = verifyJws(request);
            String username = (String) claims.get("username");

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            setAuthenticationToContext(userDetails);


        } catch (SignatureException se) {
            request.setAttribute("exception", se);
        } catch (ExpiredJwtException ee) {
            request.setAttribute("exception", ee);
        }catch (UserResignedException ue) {
            request.setAttribute("exception", ue);
        }catch (Exception e) {
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request,response);
    }

    private Map<String,Object> verifyJws(HttpServletRequest request) {
        String jws = request.getHeader("Authorization").replace("Bearer ", "");

        checkBlackList(jws);

        String base64SecretKey = jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey());
        Map<String,Object> claims = jwtTokenProvider.getClaims(jws, base64SecretKey).getBody();

        return claims;
    }

    private void setAuthenticationToContext(UserDetails userDetails) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void setAuthenticationToContext(Map<String,Object> claims) {
        String username = (String) claims.get("username");
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities((List)claims.get("roles"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return isValidAuthorization(request);
    }
    private boolean isValidAuthorization(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return authorization == null || !authorization.startsWith("Bearer");
    }

    private void checkBlackList(String jws) {
        Optional<LogoutAccessToken> optionalLogoutAccessToken = logoutAccessTokenRedisRepository.findById(jws);
        if (optionalLogoutAccessToken.isPresent())
            throw new BusinessLogicException(ExceptionCode.LOGOUT_AUTHORIZATION);
    }
}
