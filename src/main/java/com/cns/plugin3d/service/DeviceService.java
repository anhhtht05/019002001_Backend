package com.cns.plugin3d.service;

import com.cns.plugin3d.dto.*;
import com.cns.plugin3d.entity.Device;
import com.cns.plugin3d.entity.DeviceCredential;
import com.cns.plugin3d.entity.DeviceStatusHistory;
import com.cns.plugin3d.enums.StatusType;
import com.cns.plugin3d.exception.DeviceException;
import com.cns.plugin3d.helper.PagedResponseHelper;
import com.cns.plugin3d.repository.DeviceCredentialsRepository;
import com.cns.plugin3d.repository.DeviceRepository;
import com.cns.plugin3d.repository.DeviceStatusHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private final DeviceStatusHistoryRepository deviceStatusHistoryRepository;

    @Transactional
    public DeviceRegisterResponse<DeviceRegisterDetailResponse> register(DeviceRegisterRequest request) {

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
                .apiKey(UUID.randomUUID().toString())
                .secretKey(UUID.randomUUID().toString())
                .status(StatusType.ACTIVE)
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .build();

        deviceCredentialsRepository.save(credential);


        DeviceStatusHistory history = DeviceStatusHistory.builder()
                .deviceId(device.getId())
                .status(StatusType.ONLINE)
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
            Integer page, Integer limit) {

        int pageIndex = (page != null && page > 0) ? page - 1 : 0;
        int pageSize = (limit != null && limit > 0) ? limit : 20;
        PageRequest pageable = PageRequest.of(pageIndex, pageSize);

        Page<Device> resultPage;

        resultPage = deviceRepository.findAll(pageable);

        return PagedResponseHelper.build(resultPage, device -> {
            DeviceStatusHistory latestStatus = deviceStatusHistoryRepository
                    .findLatestByDeviceIdNative(device.getId());

            return DeviceResponse.builder()
                    .deviceId(device.getDeviceId())
                    .deviceName(device.getDeviceName())
                    .deviceType(device.getDeviceType())
                    .hardwareVersion(device.getHardwareVersion())
                    .serialNumber(device.getSerialNumber())
                    .macAddress(device.getMacAddress())
                    .manufacturer(device.getManufacturer())
                    .model(device.getModel())
                    .status(latestStatus != null ? latestStatus.getStatus() : StatusType.ERROR)
                    .build();


        });

    }
}