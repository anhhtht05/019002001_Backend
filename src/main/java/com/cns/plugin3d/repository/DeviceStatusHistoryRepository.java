package com.cns.plugin3d.repository;

import com.cns.plugin3d.entity.DeviceStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

public interface DeviceStatusHistoryRepository extends JpaRepository<DeviceStatusHistory, UUID> {

    @Query(value = "SELECT * FROM device_status_history " +
            "WHERE device_id = :deviceId " +
            "ORDER BY last_seen DESC LIMIT 1", nativeQuery = true)
    DeviceStatusHistory findLatestByDeviceIdNative(@Param("deviceId") UUID deviceId);
}
