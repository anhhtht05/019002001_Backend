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

    private String hardwareVersion;
    private String serialNumber;
    private String macAddress;
    private String manufacturer;
    private String model;
    private StatusDeviceType status;
}
