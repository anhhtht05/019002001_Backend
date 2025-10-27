package com.cns.plugin3d.dto;

import com.cns.plugin3d.entity.Device;
import com.cns.plugin3d.enums.StatusDeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceResponse {
    private  String id;
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String hardwareVersion;
    private String serialNumber;
    private String macAddress;
    private String manufacturer;
    private String model;
    private StatusDeviceType status;
    public static DeviceResponse fromEntity(Device device) {
        if (device == null) return null;

        return DeviceResponse.builder()
                .id(device.getId().toString())
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .deviceType(device.getDeviceType())
                .hardwareVersion(device.getHardwareVersion())
                .serialNumber(device.getSerialNumber())
                .macAddress(device.getMacAddress())
                .manufacturer(device.getManufacturer())
                .model(device.getModel())
                .status(StatusDeviceType.UNKNOWN)
                .build();
    }
}
