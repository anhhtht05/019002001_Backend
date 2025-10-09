package com.cns.plugin3d.exception;

public class AuthExceptions {

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(String message) { super(message); }
    }

    public static class UserNotActiveException extends RuntimeException {
        public UserNotActiveException(String message) { super(message); }
    }

    public static class TokenExpiredException extends RuntimeException {
        public TokenExpiredException(String message) { super(message); }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) { super(message); }
    }
}
