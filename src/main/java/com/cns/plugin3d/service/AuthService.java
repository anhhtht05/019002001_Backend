package com.cns.plugin3d.service;

import com.cns.plugin3d.dto.*;
import com.cns.plugin3d.entity.RefreshToken;
import com.cns.plugin3d.entity.UserRefreshToken;
import com.cns.plugin3d.enums.StateType;
import com.cns.plugin3d.exception.AuthExceptions;
import com.cns.plugin3d.exception.TooManyRequestsException;
import com.cns.plugin3d.repository.RefreshTokenRepository;
import com.cns.plugin3d.repository.UserRefreshTokenRepository;
import com.cns.plugin3d.util.JwtUtil;
import com.cns.plugin3d.entity.User;
import com.cns.plugin3d.enums.RoleType;
import com.cns.plugin3d.exception.CustomException;
import com.cns.plugin3d.repository.UserRepository;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RateLimiterService rateLimiterService;
    private final HttpServletRequest request;


    private final long ACCESS_TOKEN_EXP = 60;
    private final long REFRESH_TOKEN_EXP = 7 * 24 * 3600;

    public LoginResponse login(LoginRequest loginRequest) {
        Bucket bucket = rateLimiterService.resolveBucket(request);
        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException("Too many login attempts. Please try again later.");
        }
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            User user = (User) auth.getPrincipal();

            if (user.getState() != StateType.ACTIVE)
                throw new AuthExceptions.UserNotActiveException("User is inactive");

            String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), ACCESS_TOKEN_EXP);
            String refreshTokenStr = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), REFRESH_TOKEN_EXP);

            RefreshToken refreshToken = refreshTokenRepository.save(new RefreshToken(refreshTokenStr));
            userRefreshTokenRepository.save(new UserRefreshToken(user, refreshToken));
            return new LoginResponse(
                    accessToken,
                    refreshTokenStr,
                    ACCESS_TOKEN_EXP,
                    new UserDTO(user.getId(), user.getRole().name(), user.getEmail())
            );

        } catch (BadCredentialsException e) {
            throw new AuthExceptions.InvalidCredentialsException("Invalid username or password");
        }
    }
    public LoginResponse refreshToken(RefreshRequest request) {
        String token = request.getRefreshToken();

        if (!jwtUtil.validateToken(token)) {
            throw new AuthExceptions.TokenExpiredException("Refresh token expired or invalid");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthExceptions.UnauthorizedException("Invalid refresh token"));

        User user = userRefreshTokenRepository.findUserByRefreshToken(refreshToken.getId())
                .orElseThrow(() -> new AuthExceptions.UnauthorizedException("Token not linked to any user"));
        System.out.println("User::::" + user);
        String newAccess = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), ACCESS_TOKEN_EXP);

        return new LoginResponse(
                newAccess,
                token,
                ACCESS_TOKEN_EXP,
                new UserDTO(user.getId(), user.getRole().name(), user.getEmail())
        );
    }

    public UserDTO getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthExceptions.UnauthorizedException("User not found"));
        return new UserDTO(user.getId(), user.getRole().name(), user.getEmail());
    }

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException("Email already exists", HttpStatus.CONFLICT);
        }

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(RoleType.ROLE_ADMIN);
        newUser.setState(StateType.ACTIVE);

        userRepository.save(newUser);

        String accessToken = jwtUtil.generateToken(newUser.getEmail(), newUser.getRole().name(), 3600);
        String refreshTokenStr = jwtUtil.generateToken(newUser.getEmail(), newUser.getRole().name(), REFRESH_TOKEN_EXP);

        RefreshToken refreshToken = refreshTokenRepository.save(new RefreshToken(refreshTokenStr));
        userRefreshTokenRepository.save(new UserRefreshToken(newUser, refreshToken));

        return new LoginResponse(
                accessToken,
                refreshTokenStr,
                ACCESS_TOKEN_EXP,
                new UserDTO(newUser.getId(), newUser.getRole().name(), newUser.getEmail())
        );
    }
    @Transactional
    public CustomResponse updatePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return new CustomResponse(
                true,
                "Password updated successfully",
                Instant.now()
        );
    }
}
