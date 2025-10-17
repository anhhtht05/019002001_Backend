package com.cns.plugin3d.repository;
import com.cns.plugin3d.entity.FirmwareHardwareCompatibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FirmwareHardwareCompatibilityRepository extends JpaRepository<FirmwareHardwareCompatibility, UUID> {
    List<FirmwareHardwareCompatibility> findByFirmwareId(UUID firmwareId);
    void deleteByFirmwareId(UUID firmwareId);

    @Query("SELECT fhc.hardwareVersion FROM FirmwareHardwareCompatibility fhc WHERE fhc.firmwareId = :firmwareId")
    List<String> findHardwareByFirmwareId(@Param("firmwareId") UUID firmwareId);

}
