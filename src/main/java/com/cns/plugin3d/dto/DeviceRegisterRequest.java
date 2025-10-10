package com.cns.plugin3d.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceRegisterRequest {

    @NotBlank
    private String deviceId;

    @NotBlank
    private String deviceName;

    @NotBlank
    private String deviceType;

    private String hardwareVersion;
    private String serialNumber;
    private String macAddress;
    private String manufacturer;
    private String model;
}
