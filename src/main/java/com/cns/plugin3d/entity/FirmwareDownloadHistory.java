package com.cns.plugin3d.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "firmware_download_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmwareDownloadHistory {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "firmware_id", nullable = false)
    private UUID firmwareId;

    @Column(name = "downloaded_at")
    private LocalDateTime downloadedAt;

//    @Column(name = "ip_address", columnDefinition = "inet")
//    private String ipAddress;
//
//    @Column(name = "user_agent", columnDefinition = "text")
//    private String userAgent;
}
