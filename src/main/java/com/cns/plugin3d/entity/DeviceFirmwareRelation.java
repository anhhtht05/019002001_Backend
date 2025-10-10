package com.cns.plugin3d.entity;

import com.cns.plugin3d.enums.InstallationStatusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "device_firmware_relations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"device_id", "firmware_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceFirmwareRelation {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "firmware_id", nullable = false)
    private UUID firmwareId;

    @Column(name = "installed_at")
    private LocalDateTime installedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "installation_status", length = 20)
    private InstallationStatusType installationStatus =InstallationStatusType.SUCCESS;
}
