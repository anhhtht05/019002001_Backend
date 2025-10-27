package com.cns.plugin3d.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "firmware_hardware_compatibility",
        uniqueConstraints = @UniqueConstraint(columnNames = {"firmware_id", "hardware_version"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmwareHardwareCompatibility {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "firmware_id", nullable = false)
    private UUID firmwareId;

    @Column(name = "hardware_version", nullable = false, length = 50)
    private String hardwareVersion;
}
