package com.cns.plugin3d.dto;

import lombok.Data;

@Data
public class FirmwarePresignedUrlResponse {
    private String uploadUrl;
    private String fileKey;
    private long expiresIn;
}
