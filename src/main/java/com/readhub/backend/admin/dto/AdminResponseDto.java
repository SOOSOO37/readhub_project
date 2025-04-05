package com.readhub.backend.admin.dto;

import com.readhub.backend.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdminResponseDto {

    private String email;

    private User.UserStatus userStatus;
}
