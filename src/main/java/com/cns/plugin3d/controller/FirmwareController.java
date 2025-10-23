package com.cns.plugin3d.controller;

import com.cns.plugin3d.dto.*;
import com.cns.plugin3d.enums.StatusFirmwareType;
import com.cns.plugin3d.service.FirmwareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/firmware")
@RequiredArgsConstructor
public class FirmwareController {

    private final FirmwareService firmwareService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public FirmwareResponse<FirmwareMetadataResponse> uploadFirmware(
            @Valid @ModelAttribute FirmwareUploadRequest request) {
        return firmwareService.uploadFirmwareAndSaveMetadata(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public PagedResponse<FirmwareMetadataResponse> getFirmware(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "model_compat", required = false) String modelCompat,
            @RequestParam(name = "hardware_compat", required = false) String hardwareCompat,
            @RequestParam(name = "search", required = false) String search
    ) {
        return firmwareService.getFirmware(page, limit, status, modelCompat, hardwareCompat, search);
    }

    @PostMapping("/device/download")
    public FirmwareResponse<FirmwareDownloadResponse> downloadFirmware(
            @RequestHeader("Token") String token,
            @RequestHeader("X-Token") String macAddress
           ) {
        return firmwareService.getFirmwareDownloadUrl(token, macAddress);
    }

    @PutMapping("/update/{firmwareId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public FirmwareResponse<FirmwareMetadataResponse> updateFirmware( @PathVariable("firmwareId") String firmwareId,
                                                            @Valid @RequestBody FirmwareUpdateRequest request) {
        return firmwareService.updateFirmware(firmwareId, request);
    }

    @PutMapping("/update-status/{firmwareId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public FirmwareResponse<FirmwareMetadataResponse> updateFirmwareStatus(
            @PathVariable("firmwareId") String firmwareId,
            @RequestParam("status") String status) {
        return firmwareService.updateFirmwareStatus(firmwareId, status);
    }

    @DeleteMapping("/delete/{firmwareId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public FirmwareResponse deleteFirmware(@PathVariable("firmwareId") String firmwareId) {
        return firmwareService.deleteFirmware(firmwareId);
    }

}
