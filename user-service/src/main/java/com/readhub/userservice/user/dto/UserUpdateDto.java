package com.readhub.userservice.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdateDto {

    private long id;

    private String nickName;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$",
            message = "비밀번호는 숫자, 문자를 포함하여 8~20자리여야 합니다.")
    private String password;
}
