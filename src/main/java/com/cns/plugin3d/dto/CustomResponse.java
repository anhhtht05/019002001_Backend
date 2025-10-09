package com.cns.plugin3d.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class CustomResponse {
    private boolean success;
    private String message;
    private Instant timestamp;
}
