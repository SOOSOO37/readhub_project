package com.readhub.userservice.admin.dto;

import com.readhub.userservice.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdminResponseDto {

    private String email;

    private User.UserStatus userStatus;
}
