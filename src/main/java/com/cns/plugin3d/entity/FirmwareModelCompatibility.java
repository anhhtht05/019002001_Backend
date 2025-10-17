package com.cns.plugin3d.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "firmware_model_compatibility",
        uniqueConstraints = @UniqueConstraint(columnNames = {"firmware_id", "model"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmwareModelCompatibility {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "firmware_id", nullable = false)
    private UUID firmwareId;

    @Column(name = "model", nullable = false, length = 50)
    private String model;
}
