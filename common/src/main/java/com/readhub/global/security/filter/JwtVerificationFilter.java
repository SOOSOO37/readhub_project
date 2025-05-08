package com.readhub.global.security.filter;


import com.readhub.global.security.jwt.JwtTokenProvider;
import com.readhub.global.security.userdetail.CustomUserDetails;
import com.readhub.global.security.utils.CustomAuthorityUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthorityUtils authorityUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.replace("Bearer ", "");

            try {
                Claims claims = jwtTokenProvider.getClaims(
                        token, jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey())
                ).getBody();

                // 👇 userId 확인을 위한 로그 추가
                Object rawId = claims.get("id");
                System.out.println("🧾 [JwtVerificationFilter] id in claims: " + rawId);

                Long userId = null;
                if (rawId instanceof Integer) {
                    userId = ((Integer) rawId).longValue();
                } else if (rawId instanceof Long) {
                    userId = (Long) rawId;
                } else if (rawId instanceof String) {
                    userId = Long.parseLong((String) rawId);
                }

                String email = claims.getSubject();
                List<String> roles = (List<String>) claims.get("roles");

                // 👇 로그로 확인
                System.out.println("✅ Parsed email: " + email);
                System.out.println("✅ Parsed roles: " + roles);
                System.out.println("✅ Parsed userId: " + userId);

                CustomUserDetails userDetails =
                        new CustomUserDetails(userId, email, "", authorityUtils.createAuthorities(roles));
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                request.setAttribute("exception", e);
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return authorization == null || !authorization.startsWith("Bearer ");
    }
}
