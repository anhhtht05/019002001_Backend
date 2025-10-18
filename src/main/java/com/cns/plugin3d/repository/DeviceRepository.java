package com.cns.plugin3d.repository;

import com.cns.plugin3d.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
    Optional<Device> findByDeviceId(String deviceId);

    @Query("""
    SELECT d FROM Device d
    WHERE (:deviceType IS NULL OR d.deviceType = :deviceType)
      AND (:hardware IS NULL OR d.hardwareVersion = :hardware)
      AND (:model IS NULL OR d.model = :model)
      AND (
        (SELECT dsh.status FROM DeviceStatusHistory dsh
         WHERE dsh.deviceId = d.id
           AND dsh.createdAt = (
                SELECT MAX(dsh2.createdAt)
                FROM DeviceStatusHistory dsh2
                WHERE dsh2.deviceId = d.id
           )
        ) IS NULL
        OR
        (SELECT dsh.status FROM DeviceStatusHistory dsh
         WHERE dsh.deviceId = d.id
           AND dsh.createdAt = (
                SELECT MAX(dsh2.createdAt)
                FROM DeviceStatusHistory dsh2
                WHERE dsh2.deviceId = d.id
           )
        ) <> 'DELETED'
      )
""")
    Page<Device> findFilteredDevices(
            @Param("deviceType") String deviceType,
            @Param("hardware") String hardware,
            @Param("model") String model,
            Pageable pageable
    );

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
