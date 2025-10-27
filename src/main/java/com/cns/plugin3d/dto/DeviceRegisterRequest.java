package com.cns.plugin3d.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DeviceRegisterRequest {

    @NotBlank(message = "Device Id is required")
    private String deviceId;

    @NotBlank(message = "Device Name is required")
    private String deviceName;

    @NotBlank(message = "Device Type is required")
    private String deviceType;

    @NotBlank(message = "Hardware Version is required")
    private String hardwareVersion;

    private String serialNumber;

    @NotBlank(message = "Mac Address is required")
    @Pattern(
            regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$",
            message = "Invalid MAC address format"
    )
    private String macAddress;

    private String manufacturer;

    @NotBlank(message = "Model is required")
    private String model;
}
