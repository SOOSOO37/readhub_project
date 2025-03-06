package com.readhub.backend.security.handler;

import com.google.gson.Gson;
import com.readhub.backend.security.dto.LoginResponseDto;
import com.readhub.backend.user.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("# id: {}, Authenticated successfully", authentication.getName());
        sendSuccessResponse(response,authentication);

    }

    private static void sendSuccessResponse(HttpServletResponse response, Authentication authentication) throws  IOException {
        Gson gson = new Gson();
        User user = (User) authentication.getPrincipal();
        LoginResponseDto responseDto = new LoginResponseDto(user.getId(),user.getEmail(),user.getNickName());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(gson.toJson(responseDto));
    }


}
