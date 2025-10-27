package com.cns.plugin3d.dto;

import com.cns.plugin3d.enums.StatusFirmwareType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class FirmwareUpdateRequest {

    private String description;

    @NotNull(message = "Model compatibility is required")
    private List<@NotBlank String> modelCompat;

    @NotNull(message = "Hardware compatibility is required")
    private List<@NotBlank String> hardwareCompat;

}
