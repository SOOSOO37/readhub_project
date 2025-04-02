package com.readhub.backend.security.handler;

import com.readhub.backend.security.utils.ErrorResponder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        Exception exception = (Exception) request.getAttribute("exception");

        String errorMessage = (exception != null) ? "Unauthorized access attempt detected." : "Authentication failed.";

        ErrorResponder.sendErrorResponse(response, HttpStatus.UNAUTHORIZED, errorMessage);

        logExceptionMessage(authException, exception);
    }

    private void logExceptionMessage(AuthenticationException authException, Exception exception) {
        String message = Optional.ofNullable(exception)
                .map(Exception::getMessage)
                .orElse(authException.getMessage());
        log.info("Unauthorized access attempt: {}", message);
    }
}


