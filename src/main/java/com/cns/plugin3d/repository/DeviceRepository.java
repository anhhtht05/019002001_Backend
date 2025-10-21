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

    @Query(value = """
    SELECT *
    FROM devices d
    WHERE (:deviceType IS NULL OR d.device_type = :deviceType)
      AND (:hardware IS NULL OR d.hardware_version = :hardware)
      AND (:model IS NULL OR d.model = :model)
      AND (
        (SELECT dsh.status FROM device_status_history dsh
         WHERE dsh.device_id = d.id
           AND dsh.created_at = (
                SELECT MAX(dsh2.created_at)
                FROM device_status_history dsh2
                WHERE dsh2.device_id = d.id
           )
        ) IS NULL
        OR
        (SELECT dsh.status FROM device_status_history dsh
         WHERE dsh.device_id = d.id
           AND dsh.created_at = (
                SELECT MAX(dsh2.created_at)
                FROM device_status_history dsh2
                WHERE dsh2.device_id = d.id
           )
        ) <> 'DELETED'
      )
      AND (
        :search IS NULL OR (
            LOWER(d.device_name) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(d.serial_number) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(d.device_id) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(d.mac_address::text) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(d.manufacturer) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(d.model) LIKE LOWER(CONCAT('%', :search, '%'))
        )
      )
    ORDER BY d.created_at DESC
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM devices d
    WHERE (:deviceType IS NULL OR d.device_type = :deviceType)
      AND (:hardware IS NULL OR d.hardware_version = :hardware)
      AND (:model IS NULL OR d.model = :model)
      AND (
        (SELECT dsh.status FROM device_status_history dsh
         WHERE dsh.device_id = d.id
           AND dsh.created_at = (
                SELECT MAX(dsh2.created_at)
                FROM device_status_history dsh2
                WHERE dsh2.device_id = d.id
           )
        ) IS NULL
        OR
        (SELECT dsh.status FROM device_status_history dsh
         WHERE dsh.device_id = d.id
           AND dsh.created_at = (
                SELECT MAX(dsh2.created_at)
                FROM device_status_history dsh2
                WHERE dsh2.device_id = d.id
           )
        ) <> 'DELETED'
      )
      AND (
        :search IS NULL OR (
            LOWER(d.device_name) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(d.serial_number) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(d.device_id) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(d.mac_address::text) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(d.manufacturer) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(d.model) LIKE LOWER(CONCAT('%', :search, '%'))
        )
      )
    """,
            nativeQuery = true)
    Page<Device> findFilteredDevicesNative(
            @Param("deviceType") String deviceType,
            @Param("hardware") String hardware,
            @Param("model") String model,
            @Param("search") String search,
            Pageable pageable
    );



    Optional<Device> findBySerialNumber(String serialNumber);
    Optional<Device> findByMacAddress(String macAddress);
}
