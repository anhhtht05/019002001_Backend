package com.cns.plugin3d.entity;

import com.cns.plugin3d.enums.DurationType;
import com.cns.plugin3d.enums.LicenseStatus;
import com.cns.plugin3d.enums.ServiceType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "licenses")
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String deviceId;

    @Column(nullable = false, unique = true)
    private String licenseKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DurationType durationType = DurationType.FIXED;

    private Integer usageLimit;
    private Integer usageCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LicenseStatus status = LicenseStatus.ACTIVE;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL)
    private List<LicensePlan> licensePlans;

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL)
    private List<UsageLog> usageLogs;

}