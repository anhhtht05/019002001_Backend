package com.cns.plugin3d.repository;

import com.cns.plugin3d.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
    Optional<Device> findByDeviceId(String deviceId);
    Optional<Device> findBySerialNumber(String serialNumber);
    Optional<Device> findByMacAddress(String macAddress);
}
