package com.cns.plugin3d.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class CustomResponse {
    private boolean success;
    private String message;
    private Instant timestamp;
}
