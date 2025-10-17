package com.cns.plugin3d.repository;

import com.cns.plugin3d.entity.FirmwareModelCompatibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FirmwareModelCompatibilityRepository extends JpaRepository<FirmwareModelCompatibility, UUID> {
    List<FirmwareModelCompatibility> findByFirmwareId(UUID firmwareId);
    void deleteByFirmwareId(UUID firmwareId);

    @Query("SELECT fhc.model FROM FirmwareModelCompatibility fhc WHERE fhc.firmwareId = :firmwareId")
    List<String> findModelByFirmwareId(@Param("firmwareId") UUID firmwareId);
}
