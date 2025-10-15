package com.cns.plugin3d.repository;


import com.cns.plugin3d.entity.DeviceFirmwareRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceFirmwareRelationRepository extends JpaRepository<DeviceFirmwareRelation, UUID> {

    Optional<DeviceFirmwareRelation> findTopByDeviceIdOrderByInstalledAtDesc(UUID deviceId);

    Optional<DeviceFirmwareRelation> findByDeviceIdAndFirmwareId(UUID deviceId, UUID firmwareId);
}
