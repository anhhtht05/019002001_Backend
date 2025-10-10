package com.cns.plugin3d.controller;

import com.cns.plugin3d.dto.*;
import com.cns.plugin3d.service.AuthService;
import com.cns.plugin3d.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public LoginResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

//    @PostMapping("/refresh")
//    public LoginResponse refresh(@RequestBody RefreshRequest request) {
//        return authService.refreshToken(request);
//    }

    @GetMapping("/me")
    public UserDTO me(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        return authService.getMe(email);
    }
    @PutMapping("/update-password")
    @PreAuthorize("isAuthenticated()")
    public CustomResponse updatePassword(@Valid @RequestBody UpdatePasswordRequest request, Authentication authentication) {
        String email = authentication.getName();
        return authService.updatePassword(email, request.getOldPassword(), request.getNewPassword());
    }
}
