package com.cns.plugin3d.repository;

import com.cns.plugin3d.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
    Optional<Device> findByDeviceId(String deviceId);
    Optional<Device> findBySerialNumber(String serialNumber);
    Optional<Device> findByMacAddress(String macAddress);
    Page<Device> findByDeviceType(String deviceType, Pageable pageable);
    Page<Device> findByHardwareVersion(String hardwareVersion, Pageable pageable);
    Page<Device> findByModel(String model, Pageable pageable);
    Page<Device> findByDeviceTypeAndHardwareVersion(String deviceType, String hardwareVersion, Pageable pageable);
    Page<Device> findByDeviceTypeAndModel(String deviceType, String model, Pageable pageable);
    Page<Device> findByHardwareVersionAndModel(String hardwareVersion, String model, Pageable pageable);
    Page<Device> findByDeviceTypeAndHardwareVersionAndModel(
            String deviceType, String hardwareVersion, String model, Pageable pageable);
}
