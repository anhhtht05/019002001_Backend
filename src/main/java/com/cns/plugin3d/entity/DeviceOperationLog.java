package com.cns.plugin3d.entity;

import com.cns.plugin3d.enums.LogLevelType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_operation_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceOperationLog {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_level", nullable = false, length = 10)
    private LogLevelType logLevel;

    @Column(length = 100)
    private String component;

    @Column(columnDefinition = "text", nullable = false)
    private String message;

    @Column(columnDefinition = "jsonb")
    private String details;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
