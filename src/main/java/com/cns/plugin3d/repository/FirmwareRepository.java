package com.cns.plugin3d.repository;

import com.cns.plugin3d.entity.Firmware;
import com.cns.plugin3d.enums.StatusFirmwareType;
import com.cns.plugin3d.enums.StatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface FirmwareRepository extends JpaRepository<Firmware, UUID> {
    Optional<Firmware> findByVersion(String version);
    boolean existsByVersion(String version);


    @Query("""
    SELECT f FROM Firmware f
    WHERE f.status = :status
    AND EXISTS (
        SELECT 1 FROM FirmwareModelCompatibility mc
        WHERE mc.firmwareId = f.id AND mc.model = :model
    )
    AND EXISTS (
        SELECT 1 FROM FirmwareHardwareCompatibility hc
        WHERE hc.firmwareId = f.id AND hc.hardwareVersion = :hardware
    )
    ORDER BY f.createdAt DESC
""")
    Optional<Firmware> findLatestReleasedFirmwareByModelAndHardware(
            @Param("model") String model,
            @Param("hardware") String hardware,
            @Param("status") StatusFirmwareType status
    );


    @Query(value = """
    SELECT DISTINCT ON (f.id) f.*
    FROM firmwares f
    LEFT JOIN firmware_model_compatibility fmc ON fmc.firmware_id = f.id
    LEFT JOIN firmware_hardware_compatibility fhc ON fhc.firmware_id = f.id
    WHERE f.status <> 'DELETED'
      AND (:status IS NULL OR f.status = :status)
      AND (:modelCompat IS NULL OR fmc.model = :modelCompat)
      AND (:hardwareCompat IS NULL OR fhc.hardware_version = :hardwareCompat)
      AND (
        :search IS NULL OR (
          LOWER(f.name) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(f.version) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(f.description) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(f.file_path) LIKE LOWER(CONCAT('%', :search, '%'))
        )
      )
    ORDER BY f.id,
      COALESCE(NULLIF(SPLIT_PART(REGEXP_REPLACE(f.version, '[^0-9\\.]', '', 'g'), '.', 1), '')::int, 0),
      COALESCE(NULLIF(SPLIT_PART(REGEXP_REPLACE(f.version, '[^0-9\\.]', '', 'g'), '.', 2), '')::int, 0),
      COALESCE(NULLIF(SPLIT_PART(REGEXP_REPLACE(f.version, '[^0-9\\.]', '', 'g'), '.', 3), '')::int, 0)
    """,
            countQuery = """
    SELECT COUNT(DISTINCT f.id)
    FROM firmwares f
    LEFT JOIN firmware_model_compatibility fmc ON fmc.firmware_id = f.id
    LEFT JOIN firmware_hardware_compatibility fhc ON fhc.firmware_id = f.id
    WHERE f.status <> 'DELETED'
      AND (:status IS NULL OR f.status = :status)
      AND (:modelCompat IS NULL OR fmc.model = :modelCompat)
      AND (:hardwareCompat IS NULL OR fhc.hardware_version = :hardwareCompat)
      AND (
        :search IS NULL OR (
          LOWER(f.name) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(f.version) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(f.description) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(f.file_path) LIKE LOWER(CONCAT('%', :search, '%'))
        )
      )
    """,
            nativeQuery = true)
    Page<Firmware> findFilteredFirmwaresNative(
            @Param("status") String status,
            @Param("modelCompat") String modelCompat,
            @Param("hardwareCompat") String hardwareCompat,
            @Param("search") String search,
            Pageable pageable
    );




}
