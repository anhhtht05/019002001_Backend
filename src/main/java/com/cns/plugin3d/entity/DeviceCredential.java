package com.cns.plugin3d.entity;

import com.cns.plugin3d.enums.StatusDeviceCredentialType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceCredential {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "api_key", nullable = false, length = 100)
    private String apiKey;

    @Column(name = "secret_key", length = 100)
    private String secretKey;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusDeviceCredentialType status = StatusDeviceCredentialType.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
