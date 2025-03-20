package com.readhub.backend.user.controller;

import com.readhub.backend.global.utils.UriCreator;
import com.readhub.backend.security.utils.CustomAuthorityUtils;
import com.readhub.backend.user.dto.UserCreateDto;
import com.readhub.backend.user.dto.UserResponseDto;
import com.readhub.backend.user.dto.UserUpdateDto;
import com.readhub.backend.user.entity.User;
import com.readhub.backend.user.mapper.UserMapper;
import com.readhub.backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@Validated
@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserMapper mapper;

    private final UserService service;

    private final CustomAuthorityUtils customAuthorityUtils;

    private final static String USER_DEFAULT_URL = "/users";

    @PostMapping("/sign-up")
    public ResponseEntity createUser(@Valid @RequestBody UserCreateDto userCreateDto){

        User user = service.createUser(mapper.userCreateDtoToUser(userCreateDto));
        URI location = UriCreator.createUri(USER_DEFAULT_URL, user.getId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/edit/nickname")
    public ResponseEntity updateNickName(@RequestBody UserUpdateDto userUpdateDto,
                                         @AuthenticationPrincipal User user){

        userUpdateDto.setId(user.getId());
        User findUser = service.updateNickName(mapper.userUpdateDtoToUser(userUpdateDto));
        UserResponseDto response = mapper.userToUserResponseDto(findUser);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PatchMapping("/edit/password")
    public ResponseEntity updatePassword(@Valid @RequestBody UserUpdateDto userUpdateDto,
                                         @AuthenticationPrincipal User user) {

        userUpdateDto.setId(user.getId());
        User findUser = service.updatePassword(mapper.userUpdateDtoToUser(userUpdateDto));
        UserResponseDto response = mapper.userToUserResponseDto(findUser);
        return new ResponseEntity(response,HttpStatus.OK);
    }

    @GetMapping("/my-page")
    public ResponseEntity findUser(@AuthenticationPrincipal User user) {

        User findUser = service.findUser(user);
        UserResponseDto response = mapper.userToUserResponseDto(findUser);

        return new ResponseEntity(response,HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteUser(@AuthenticationPrincipal User user) {
        service.deleteUser(user);

        return ResponseEntity.noContent().build();
    }
}
