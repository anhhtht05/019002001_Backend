package com.cns.plugin3d.entity;

import com.cns.plugin3d.enums.StatusDeviceCampainType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "campaign_device_relations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"campaign_id", "device_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignDeviceRelation {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusDeviceCampainType status = StatusDeviceCampainType.PENDING;

    private Integer progress;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
