package com.readhub.userservice.admin.controller;

import com.readhub.userservice.admin.dto.AdminResponseDto;
import com.readhub.userservice.admin.mapper.AdminMapper;
import com.readhub.userservice.admin.service.AdminService;
import com.readhub.global.response.MultiResponseDto;
import com.readhub.userservice.user.dto.UserResponseDto;
import com.readhub.userservice.user.entity.User;
import com.readhub.userservice.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/admins")
@RestController
public class AdminController {

    private final AdminService adminService;
    private final UserMapper userMapper;
    private final AdminMapper adminMapper;

    @GetMapping("/find")
    public ResponseEntity findUsers(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {

        Page<User> userPage = adminService.findUsers(page - 1, size);
        List<User> userList = userPage.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(userMapper.usersToUserResponseDtos(userList), userPage), HttpStatus.OK);
    }

    @GetMapping("/email")
    public ResponseEntity<UserResponseDto> findUserByEmail(@RequestParam String email) {
        User user = adminService.findUserByEmail(email);
        return new ResponseEntity<>(userMapper.userToUserResponseDto(user), HttpStatus.OK);
    }

    @PatchMapping("/stop/{user-id}")
    public ResponseEntity stopUser(@PathVariable("user-id") long id) {
        User updateUser = adminService.stopUser(id);
        AdminResponseDto response = adminMapper.userToAdminResponseDto(updateUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
