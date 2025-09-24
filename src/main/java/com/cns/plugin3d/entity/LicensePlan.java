package com.cns.plugin3d.entity;

import com.cns.plugin3d.enums.ServiceType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "license_plans")
public class LicensePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "license_id")
    private License license;

    @Column(nullable = false)
    private String name;

    private Integer durationDays;
    private Integer maxUsage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

}