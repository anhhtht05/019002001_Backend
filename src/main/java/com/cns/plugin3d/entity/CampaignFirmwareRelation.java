package com.cns.plugin3d.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "campaign_firmware_relations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"campaign_id", "firmware_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignFirmwareRelation {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Column(name = "firmware_id", nullable = false)
    private UUID firmwareId;
}
