package com.healthdata.service;

import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

import com.healthdata.dto.UserDto;
import com.healthdata.entity.User;
import com.healthdata.repository.UserRepository;
import com.healthdata.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스.
 * 사용자 등록, 로그인, 현재 사용자 정보 조회 등의 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final MessageSource messageSource;

    /**
     * 새로운 사용자를 시스템에 등록합니다.
     * 이메일과 닉네임의 중복 여부를 확인합니다.
     *
     * @param request 사용자 등록 요청 DTO
     * @throws IllegalArgumentException 이메일 또는 닉네임이 이미 존재할 경우
     */
    public void registerUser(UserDto.RegistrationRequest request) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException(messageSource.getMessage("user.email.exists", null, LocaleContextHolder.getLocale()));
        }
        // 닉네임 중복 확인
        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new IllegalArgumentException(messageSource.getMessage("user.nickname.exists", null, LocaleContextHolder.getLocale()));
        }

        // timezoneId 유효성 검사
        if (request.getTimezoneId() == null || request.getTimezoneId().isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage("user.timezone.required", null, LocaleContextHolder.getLocale()));
        }
        try {
            ZoneId.of(request.getTimezoneId());
        } catch (ZoneRulesException e) {
            throw new IllegalArgumentException(messageSource.getMessage("user.timezone.invalid", new Object[]{request.getTimezoneId()}, LocaleContextHolder.getLocale()));
        }

        // 사용자 엔티티 빌드 및 비밀번호 인코딩
        User user = User.builder()
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // 비밀번호 암호화
                .timezoneId(request.getTimezoneId()) // timezoneId 할당
                .build();

        userRepository.save(user); // 사용자 정보 저장
    }

    /**
     * 사용자를 인증하고 JWT 토큰을 발급합니다.
     *
     * @param request 사용자 로그인 요청 DTO (이메일, 비밀번호)
     * @return 로그인 응답 DTO (JWT 토큰 포함)
     */
    public UserDto.LoginResponse loginUser(UserDto.LoginRequest request) {
        log.info("사용자 로그인 시도: {}", request.getEmail());
        // Spring Security의 AuthenticationManager를 통해 사용자 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 인증된 사용자를 SecurityContext에 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String jwt = tokenProvider.generateToken((UserDetails) authentication.getPrincipal());
        return new UserDto.LoginResponse(jwt);
    }

    /**
     * 현재 인증된 사용자의 정보를 조회합니다。
     *
     * @return 현재 인증된 사용자 엔티티
     * @throws IllegalArgumentException 현재 사용자를 찾을 수 없을 경우
     */
    public User getCurrentUser() {
        // SecurityContext에서 현재 인증된 사용자의 이메일(principal)을 가져옴
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        // 이메일을 통해 사용자 정보를 조회, 없으면 예외 발생
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(messageSource.getMessage("user.notfound", null, LocaleContextHolder.getLocale())));
    }
}
