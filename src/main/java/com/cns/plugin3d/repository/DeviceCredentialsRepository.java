package com.cns.plugin3d.repository;

import com.cns.plugin3d.entity.DeviceCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeviceCredentialsRepository extends JpaRepository<DeviceCredential, UUID> {
}
