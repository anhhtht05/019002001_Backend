package com.cns.plugin3d.service;

import com.cns.plugin3d.dto.*;
import com.cns.plugin3d.entity.Device;
import com.cns.plugin3d.entity.DeviceCredential;
import com.cns.plugin3d.entity.DeviceStatusHistory;
import com.cns.plugin3d.enums.StatusDeviceType;
import com.cns.plugin3d.enums.StatusDeviceCredentialType;
import com.cns.plugin3d.exception.DeviceException;
import com.cns.plugin3d.helper.PagedResponseHelper;
import com.cns.plugin3d.repository.DeviceCredentialsRepository;
import com.cns.plugin3d.repository.DeviceRepository;
import com.cns.plugin3d.repository.DeviceStatusHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private final DeviceStatusHistoryRepository deviceStatusHistoryRepository;

    @Value("${spring.apikey}")
    private String apiKey;

    @Transactional
    public DeviceRegisterResponse<DeviceRegisterDetailResponse>
    register(DeviceRegisterRequest request) {

        deviceRepository.findByDeviceId(request.getDeviceId())
                .ifPresent(d -> {
                    throw new DeviceException(
                            "DEVICE_ALREADY_EXISTS",
                            "Device " + request.getDeviceId() + " is already registered",
                            "Device " + request.getDeviceId() + " is already registered in system"
                    );
                });

        if (request.getSerialNumber() != null) {
            deviceRepository.findBySerialNumber(request.getSerialNumber())
                    .ifPresent(d -> {
                        throw new DeviceException(
                                "SERIAL_ALREADY_EXISTS",
                                "Serial " + request.getSerialNumber() + " is already registered",
                                "Serial " + request.getSerialNumber() + " is already registered in system"
                        );
                    });
        }

        if (request.getMacAddress() != null) {
            deviceRepository.findByMacAddress(request.getMacAddress())
                    .ifPresent(d -> {
                        throw new DeviceException(
                                "MAC_ALREADY_EXISTS",
                                "MAC " + request.getMacAddress() + " is already registered",
                                "MAC " + request.getMacAddress() + " is already registered in system"
                        );
                    });
        }

        Device device = Device.builder()
                .deviceId(request.getDeviceId())
                .deviceName(request.getDeviceName())
                .deviceType(request.getDeviceType())
                .hardwareVersion(request.getHardwareVersion())
                .serialNumber(request.getSerialNumber())
                .macAddress(request.getMacAddress())
                .manufacturer(request.getManufacturer())
                .model(request.getModel())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        deviceRepository.save(device);

        LocalDateTime expiresAt = LocalDateTime.now().plusYears(1);
        DeviceCredential credential = DeviceCredential.builder()
                .deviceId(device.getId())
                .apiKey(apiKey)
                .secretKey(null)
                .status(StatusDeviceCredentialType.ACTIVE)
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .build();

        deviceCredentialsRepository.save(credential);


        DeviceStatusHistory history = DeviceStatusHistory.builder()
                .deviceId(device.getId())
                .status(StatusDeviceType.UNKNOWN)
//                .ipAddress(httpRequest.getRemoteAddr())
                .lastSeen(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        deviceStatusHistoryRepository.save(history);

        DeviceRegisterDetailResponse data = DeviceRegisterDetailResponse.builder()
                .deviceId(device.getDeviceId())
                .expiresAt(expiresAt)
                .status(credential.getStatus().name())
                .build();

        return DeviceRegisterResponse.<DeviceRegisterDetailResponse>builder()
                .success(true)
                .data(data)
                .message("Device registered successfully")
                .build();
    }


    public PagedResponse<DeviceResponse> getDevice(
            Integer page, Integer limit, String deviceType, String hardware, String model, String search) {

        int pageIndex = (page != null && page > 0) ? page - 1 : 0;
        int pageSize = (limit != null && limit > 0) ? limit : 20;
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        Page<Device> resultPage = deviceRepository.findFilteredDevicesNative(
                deviceType, hardware, model, search, pageable
        );

        return PagedResponseHelper.build(resultPage, DeviceResponse::fromEntity);
    }

    public DeviceRegisterResponse<DeviceResponse> updateDevice(DeviceUpdateRequest request) {
        Device device = deviceRepository.findByDeviceId(request.getDeviceId())
                .orElseThrow(() -> new RuntimeException("Device not found with ID: " + request.getDeviceId()));

        device.setDeviceName(request.getDeviceName());
        device.setDeviceType(request.getDeviceType());
        device.setHardwareVersion(request.getHardwareVersion());
        device.setSerialNumber(request.getSerialNumber());
        device.setMacAddress(request.getMacAddress());
        device.setManufacturer(request.getManufacturer());
        device.setModel(request.getModel());
        device.setUpdatedAt(LocalDateTime.now());
        DeviceStatusHistory latestStatus = deviceStatusHistoryRepository
                .findLatestByDeviceIdNative(device.getId());
        latestStatus.setStatus(request.getStatus());

        Device deviceUpdate = deviceRepository.save(device);

        DeviceResponse data = DeviceResponse.builder()
                .id(deviceUpdate.getId().toString())
                .deviceId(deviceUpdate.getDeviceId())
                .deviceName(deviceUpdate.getDeviceName())
                .deviceType(deviceUpdate.getDeviceType())
                .hardwareVersion(deviceUpdate.getHardwareVersion())
                .serialNumber(deviceUpdate.getSerialNumber())
                .macAddress(deviceUpdate.getMacAddress())
                .manufacturer(deviceUpdate.getManufacturer())
                .model(deviceUpdate.getModel())
                .status(latestStatus != null ? latestStatus.getStatus() : StatusDeviceType.ERROR)
                .build();
    return DeviceRegisterResponse.<DeviceResponse>builder()
                .success(true)
                .data(data)
                .message("Device updated successfully")
                .build();
    }

    @Transactional
    public CustomResponse deleteDevice(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new DeviceException(
                        "DEVICE_NOT_FOUND",
                        "Device not found",
                        "Device with ID " + deviceId + " does not exist"
                ));

        deviceRepository.delete(device);

        return CustomResponse.builder()
                .success(true)
                .message("Device deleted successfully: " + deviceId)
                .timestamp(Instant.now())
                .build();
    }

}