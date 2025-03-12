package com.readhub.backend.security.dto;

import com.readhub.backend.security.utils.OauthProvider;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Oauth {

    @NotNull
    private OauthProvider provider;

    @NotNull
    private String code;
}
