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


    @Query(value = """
    SELECT * FROM firmwares f
    WHERE f.status = :status
      AND EXISTS (
          SELECT 1 FROM firmware_model_compatibility mc
          WHERE mc.firmware_id = f.id AND mc.model = :model
      )
      AND EXISTS (
          SELECT 1 FROM firmware_hardware_compatibility hc
          WHERE hc.firmware_id = f.id AND hc.hardware_version = :hardware
      )
    ORDER BY
        CAST(SPLIT_PART(REPLACE(f.version, 'v', ''), '.', 1) AS INTEGER) DESC,
        CAST(SPLIT_PART(REPLACE(f.version, 'v', ''), '.', 2) AS INTEGER) DESC,
        CAST(SPLIT_PART(REPLACE(f.version, 'v', ''), '.', 3) AS INTEGER) DESC
    LIMIT 1
    """, nativeQuery = true)
    Optional<Firmware> findLatestReleasedFirmwareByModelAndHardware(
            @Param("model") String model,
            @Param("hardware") String hardware,
            @Param("status") String status
    );


    @Query(value = """
            SELECT * FROM (
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
              ORDER BY f.id, f.created_at DESC
            ) AS sub
            ORDER BY sub.created_at DESC;
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
