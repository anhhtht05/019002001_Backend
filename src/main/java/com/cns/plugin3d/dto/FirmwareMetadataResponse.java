package com.cns.plugin3d.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmwareMetadataResponse {
    private String id;
    private String name;
    private String version;
    private String description;
    private String filePath;
    private Long fileSize;
    private String status;
    private List<String> modelCompat;
    private List<String> hardwareCompat;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
