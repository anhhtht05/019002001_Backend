package com.cns.plugin3d.dto;

import com.cns.plugin3d.enums.StateType;
import com.cns.plugin3d.enums.StatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceResponse {
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String hardwareVersion;
    private String serialNumber;
    private String macAddress;
    private String manufacturer;
    private String model;
    private StatusType status;
}
