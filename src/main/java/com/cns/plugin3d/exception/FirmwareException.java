package com.cns.plugin3d.exception;

public class FirmwareException extends RuntimeException {
    private final String code;

    private final String details;

    public FirmwareException(String code, String message, String details) {
        super(message);
        this.code = code;
        this.details = details;
    }

    public String getCode() {
        return code;
    }
    public String getDetails() {
        return details;
    }
}
