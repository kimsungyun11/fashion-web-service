package com.fashionNav.controller;


import com.fashionNav.common.api.Api;
import com.fashionNav.model.dto.request.UserLoginRequest;
import com.fashionNav.model.dto.request.UserRegisterRequest;
import com.fashionNav.model.dto.request.UserUpdateRequest;
import com.fashionNav.model.dto.response.UserAuthenticationResponse;
import com.fashionNav.model.dto.response.UserRegistrationResponse;
import com.fashionNav.model.dto.response.UserResponse;
import com.fashionNav.model.entity.User;
import com.fashionNav.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원 가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/register")
    public Api<UserResponse> createUser(@Valid @RequestBody UserRegisterRequest request) {
        UserRegistrationResponse response = userService.register(request);
        return Api.OK(UserResponse.builder()
                .userId(response.getUserId())
                .name(response.getName())
                .email(response.getEmail())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build());
    }

    @Operation(summary = "사용자 인증", description = "사용자 로그인 및 JWT 토큰을 발급합니다.")
    @PostMapping("/authenticate")
    public Api<UserAuthenticationResponse> authenticate(@Valid @RequestBody UserLoginRequest request) {
        var response = userService.authenticate(request);
        return Api.OK(response);
    }

    @Operation(summary = "리프레시 토큰", description = "리프레시 토큰을 사용해 새로운 액세스 토큰을 발급합니다.")
    @PostMapping("/refresh")
    public UserAuthenticationResponse refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return userService.refreshToken(refreshToken);
    }

    @Operation(summary = "사용자 조회", description = "특정 ID를 가진 사용자의 정보를 조회합니다.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{userId}")
    public Api<UserResponse> getUser(@PathVariable int userId) {
        var response = userService.getUserId(userId);

        return Api.OK(response);
    }

    @Operation(summary = "회원 정보 수정", description = "현재 인증된 사용자의 정보를 수정합니다.",security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/me")
    public Api<UserResponse> updateUser(Authentication authentication, @Valid @RequestBody UserUpdateRequest request) {
        var response = userService.updateUser((User)authentication.getPrincipal(), request);

        return Api.OK(response);
    }

    @Operation(summary = "회원 탈퇴", description = "사용자 정보를 삭제합니다.")
    @PreAuthorize("authentication.principal.userId == #userId")
    @DeleteMapping("/{userId}")
    public Api<String> deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);

        return Api.OK("회원 탈퇴가 완료되었습니다");
    }
}


