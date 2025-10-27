package com.cns.plugin3d.repository;

import com.cns.plugin3d.entity.Firmware;
import com.cns.plugin3d.entity.FirmwareDownloadHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface FirmwareDownloadHistoryRepository extends JpaRepository<FirmwareDownloadHistory, UUID> {
    @Query(value = """
    SELECT f.* FROM firmware_download_history h
    JOIN firmwares f ON h.firmware_id = f.id
    WHERE h.device_id = :deviceId
    ORDER BY
        CAST(SPLIT_PART(REPLACE(f.version, 'v', ''), '.', 1) AS INTEGER) DESC,
        CAST(SPLIT_PART(REPLACE(f.version, 'v', ''), '.', 2) AS INTEGER) DESC,
        CAST(SPLIT_PART(REPLACE(f.version, 'v', ''), '.', 3) AS INTEGER) DESC
    LIMIT 1
    """, nativeQuery = true)
    Optional<Firmware> findLatestDownloadedFirmwareByDeviceId(@Param("deviceId") UUID deviceId);

}
