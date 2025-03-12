package com.readhub.backend.security.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.readhub.backend.security.dto.UserProfile;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OauthProvider implements BaseEnum{

    KAKAO("kakao", (attributes) -> {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        return UserProfile.builder()
                .email((String) kakaoAccount.get("email"))
                .gender((String) kakaoAccount.get("gender"))
                .build();
    });

    private final String registrationId;
    private final Function<Map<String, Object>, UserProfile> of;

    OauthProvider(String registrationId, Function<Map<String, Object>, UserProfile> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static UserProfile extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of.apply(attributes);
    }

    @Override
    @JsonValue
    public String getName() {
        return this.registrationId;
    }

    @Override
    public String getDescription() {
        return this.registrationId;
    }

    @JsonCreator
    public static OauthProvider from(String value) {
        for (OauthProvider provider : OauthProvider.values()) {
            if (provider.getName().equalsIgnoreCase(value)) {
                return provider;
            }
        }
        return null;
     }
    }