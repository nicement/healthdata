package com.healthdata.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistrationRequest {
        private String name;
        private String nickname;
        private String email;
        private String password;
        private String timezoneId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter
    @AllArgsConstructor
    public static class LoginResponse {
        private String accessToken;
    }
}
