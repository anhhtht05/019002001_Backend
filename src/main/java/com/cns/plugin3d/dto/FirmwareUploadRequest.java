package com.cns.plugin3d.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FirmwareUploadRequest {

    @NotBlank(message = "Firmware name is required")
    private String firmwareName;

    @NotBlank(message = "Version is required")
//    @Pattern(regexp = "^(v)?\\d+(\\.\\d+){1,2}$", message = "Invalid version format (ex: 1.0.0 or v1.0.0)")
    private String version;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "File is required")
    private MultipartFile file;

}
