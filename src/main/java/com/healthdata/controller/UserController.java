package com.healthdata.controller;

import com.healthdata.dto.UserDto;
import com.healthdata.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 관련 API를 처리하는 컨트롤러.
 * 사용자 등록 및 로그인 기능을 제공합니다.
 */
@Tag(name = "User API", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 새로운 사용자를 등록합니다.
     *
     * @param request 사용자 등록에 필요한 정보 (이름, 닉네임, 이메일, 비밀번호)
     * @return 성공 시 200 OK 응답
     */
    @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 등록 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @RequestBody(
		description = "사용자 등록 요청 본문",
		required = true,
		content = @Content(
			mediaType = "application/json",
			examples = @ExampleObject(
				name = "사용자 등록 예시",
				summary = "새로운 사용자 등록",
				value = "{\"name\": \"홍길동\", \"nickname\": \"의적\", \"email\": \"hong@example.com\", \"password\": \"password123\"}"
			)
		)
    )
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@org.springframework.web.bind.annotation.RequestBody UserDto.RegistrationRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 사용자를 로그인하고 JWT 토큰을 발급합니다.
     *
     * @param request 사용자 로그인 정보 (이메일, 비밀번호)
     * @return 로그인 성공 시 JWT 토큰을 포함한 응답
     */
    @Operation(summary = "사용자 로그인", description = "사용자 로그인 및 JWT 토큰 발급")
    @ApiResponse(responseCode = "200", description = "로그인 성공 및 토큰 발급")
    @ApiResponse(responseCode = "401", description = "인증 실패 (잘못된 이메일 또는 비밀번호)")
    @RequestBody(
		description = "사용자 로그인 요청 본문",
		required = true,
		content = @Content(
			mediaType = "application/json",
			examples = @ExampleObject(
				name = "사용자 로그인 예시",
				summary = "기존 사용자 로그인",
				value = "{\"email\": \"hong@example.com\", \"password\": \"password123\"}"
			)
		)
    )
    @PostMapping("/login")
    public ResponseEntity<UserDto.LoginResponse> loginUser(@org.springframework.web.bind.annotation.RequestBody UserDto.LoginRequest request) {
        UserDto.LoginResponse response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }
}
