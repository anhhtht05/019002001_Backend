package com.cns.plugin3d.entity;

import com.cns.plugin3d.enums.StateType;
import com.cns.plugin3d.enums.StatusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "firmwares")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "status <> 'DELETED'")
public class Firmware {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 50)
    private String version;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(length = 64)
    private String checksum;

    @Column(name = "release_notes", columnDefinition = "text")
    private String releaseNotes;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusType status;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
