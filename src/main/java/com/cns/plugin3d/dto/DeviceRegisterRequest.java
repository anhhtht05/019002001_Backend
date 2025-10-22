package com.cns.plugin3d.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Serial Number is required")
    private String serialNumber;

    @NotBlank(message = "Mac Address is required")
    private String macAddress;

    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;

    @NotBlank(message = "Model is required")
    private String model;
}
