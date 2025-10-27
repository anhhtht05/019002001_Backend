package com.cns.plugin3d.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class FirmwareUploadRequest {

    @NotBlank(message = "Firmware name is required")
    private String firmwareName;

    @NotBlank(message = "Version is required")
    @Pattern(regexp = "^(v)\\d+(\\.\\d+){1,2}$", message = "Invalid version format (v1.0.0)")
    private String version;

    private String description;

    @NotNull(message = "Model compatibility is required")
    private List<@NotBlank String> modelCompat;

    @NotNull(message = "Hardware compatibility is required")
    private List<@NotBlank String> hardwareCompat;

    @NotNull(message = "File is required")
    private MultipartFile file;

}
