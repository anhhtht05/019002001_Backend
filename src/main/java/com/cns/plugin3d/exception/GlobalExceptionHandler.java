package com.cns.plugin3d.exception;

import com.cns.plugin3d.dto.DeviceRegisterResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> buildResponse(String error, String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", error);
        body.put("message", message);
        body.put("timestamp", Instant.now().toString());
        return new ResponseEntity<>(body, status);
    }
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = Map.of(
                "error", status.name(),
                "message", message,
                "timestamp", Instant.now().toString()
        );
        return new ResponseEntity<>(body, status);
    }


    @ExceptionHandler(DeviceException.class)
    public ResponseEntity<DeviceRegisterResponse<?>> handleDeviceException(DeviceException ex) {
        DeviceRegisterResponse<?> response = DeviceRegisterResponse.builder()
                .success(false)
                .error(new DeviceRegisterResponse.ApiError(ex.getCode(), ex.getMessage(), ex.getDetails()))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        return buildResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(AuthExceptions.InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(AuthExceptions.InvalidCredentialsException ex) {
        return buildResponse("UNAUTHORIZED", ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthExceptions.UserNotActiveException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotActive(AuthExceptions.UserNotActiveException ex) {
        return buildResponse("FORBIDDEN", ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthExceptions.TokenExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleTokenExpired(AuthExceptions.TokenExpiredException ex) {
        return buildResponse("UNAUTHORIZED", ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthExceptions.UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(AuthExceptions.UnauthorizedException ex) {
        return buildResponse("UNAUTHORIZED", ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // Optional: catch-all
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        return buildResponse("ERROR", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

