package com.cns.plugin3d.entity;

import com.cns.plugin3d.enums.StateType;
import com.cns.plugin3d.enums.StatusType;
import com.cns.plugin3d.util.InetAddressConverter;
import com.vladmihalcea.hibernate.type.basic.Inet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceStatusHistory {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatusType status;

//    @Column(name = "ip_address", columnDefinition = "inet")
//    private String ipAddress;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
