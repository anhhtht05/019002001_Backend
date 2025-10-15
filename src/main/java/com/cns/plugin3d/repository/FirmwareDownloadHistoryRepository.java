package com.cns.plugin3d.repository;

import com.cns.plugin3d.entity.FirmwareDownloadHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FirmwareDownloadHistoryRepository extends JpaRepository<FirmwareDownloadHistory, UUID> {
}
