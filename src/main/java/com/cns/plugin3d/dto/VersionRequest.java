package com.cns.plugin3d.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VersionRequest {

    @NotBlank(message = "Version is required")
//    @Pattern(regexp = "^(v)?\\d+(\\.\\d+){1,2}$", message = "Invalid version format (ex: 1.0.0 or v1.0.0)")
    private String version;
}
