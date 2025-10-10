package com.cns.plugin3d.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DeviceRegisterDetailResponse {
    private String deviceId;
    private LocalDateTime expiresAt;
    private String status;
}
