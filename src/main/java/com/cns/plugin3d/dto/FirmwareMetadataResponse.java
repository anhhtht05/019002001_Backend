package com.cns.plugin3d.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmwareMetadataResponse {
    private String name;
    private String version;
    private String description;
    private String filePath;
    private Long fileSize;
    private String checksum;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
