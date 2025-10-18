package com.cns.plugin3d.controller;

import com.cns.plugin3d.dto.*;
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
            @ModelAttribute FirmwareUploadRequest request) {
        return firmwareService.uploadFirmwareAndSaveMetadata(request);
    }
//    @PostMapping("/presign")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
//    public FirmwareResponse<FirmwarePresignedUrlResponse> createPresignedUrl(
//            @RequestBody VersionRequest request) {
//      return firmwareService.generatePresignedUrl(request);
//    }
//
//    @PostMapping("/update")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
//    public FirmwareResponse<FirmwareMetadataResponse> saveFirmwareMetadata(
//            @RequestBody FirmwareUploadRequest request) {
//        String fileKey = "firmware/" + request.getVersion() + "/" + request.getFileName();
//         return firmwareService.saveFirmwareMetadata(request, fileKey);
//    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public PagedResponse<FirmwareMetadataResponse> getFirmware(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit
    ) {
        return firmwareService.getFirmware(page, limit);
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
    public FirmwareResponse<FirmwareMetadataResponse> updateDevice( @PathVariable("firmwareId") String firmwareId,
                                                            @Valid @RequestBody FirmwareUpdateRequest request) {
        return firmwareService.updateFirmware(firmwareId, request);
    }

}
