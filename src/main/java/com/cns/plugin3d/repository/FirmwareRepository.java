package com.cns.plugin3d.repository;

import com.cns.plugin3d.entity.Firmware;
import com.cns.plugin3d.enums.StatusType;
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
            @Param("status") StatusType status
    );

}
