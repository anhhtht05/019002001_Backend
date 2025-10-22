package com.cns.plugin3d.dto;

import com.cns.plugin3d.enums.StatusDeviceType;
import com.cns.plugin3d.enums.StatusType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceUpdateRequest {

    @NotBlank(message = "Device ID is required")
    private String deviceId;

    @NotBlank(message = "Device name is required")
    private String deviceName;

    @NotBlank(message = "Device type is required")
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

    private StatusDeviceType status;
}
