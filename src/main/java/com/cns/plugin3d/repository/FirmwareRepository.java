package com.cns.plugin3d.repository;

import com.cns.plugin3d.entity.Firmware;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface FirmwareRepository extends JpaRepository<Firmware, UUID> {
    Optional<Firmware> findByVersion(String version);
    boolean existsByVersion(String version);
}
