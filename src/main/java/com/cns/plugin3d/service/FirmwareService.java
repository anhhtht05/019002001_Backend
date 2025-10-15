package com.cns.plugin3d.service;

import com.cns.plugin3d.dto.*;
import com.cns.plugin3d.entity.Device;
import com.cns.plugin3d.entity.DeviceFirmwareRelation;
import com.cns.plugin3d.entity.Firmware;
import com.cns.plugin3d.entity.FirmwareDownloadHistory;
import com.cns.plugin3d.enums.StatusType;
import com.cns.plugin3d.helper.PagedResponseHelper;
import com.cns.plugin3d.repository.DeviceFirmwareRelationRepository;
import com.cns.plugin3d.repository.DeviceRepository;
import com.cns.plugin3d.repository.FirmwareDownloadHistoryRepository;
import com.cns.plugin3d.repository.FirmwareRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FirmwareService {

    private final FirmwareRepository firmwareRepository;
    private final S3Service s3Service;
    private final DeviceRepository deviceRepository;
    private final FirmwareDownloadHistoryRepository downloadHistoryRepository;
    private final DeviceFirmwareRelationRepository deviceFirmwareRelationRepository;

    @Transactional
    public FirmwareResponse<FirmwareMetadataResponse> uploadFirmwareAndSaveMetadata(FirmwareUploadRequest request) {
        try {
            if (request.getFile() == null || request.getFile().isEmpty()) {
                return FirmwareResponse.error("EMPTY_FILE", "Firmware file cannot be empty", null);
            }
            if (firmwareRepository.existsByVersion(request.getVersion())) {
                return FirmwareResponse.error("VERSION_EXISTS",
                        "Firmware version already exists: " + request.getVersion(), null);
            }

            String key = String.format("%s/%s/%s",
                    s3Service.getFirmwarePrefix(),
                    request.getVersion(),
                    request.getFile().getOriginalFilename());

            String uploadedUrl = s3Service.uploadFile(request.getFile(),key);

            boolean exists = s3Service.exists(key);
            if (!exists) {
                return FirmwareResponse.error("UPLOAD_FAILED",
                        "File not found on S3 after upload", null);
            }

            String checksum = calculateSHA256(request.getFile());

            Firmware firmware = new Firmware();
            firmware.setName(request.getFirmwareName());
            firmware.setVersion(request.getVersion());
            firmware.setDescription(request.getDescription());
            firmware.setFilePath(key);
            firmware.setFileSize(request.getFile().getSize());
            firmware.setChecksum(checksum);
            firmware.setStatus(StatusType.DRAFT);
            firmware.setCreatedAt(LocalDateTime.now());
            firmware.setUpdatedAt(LocalDateTime.now());

            firmwareRepository.save(firmware);

            FirmwareMetadataResponse metadata = FirmwareMetadataResponse.builder()
                    .name(firmware.getName())
                    .version(firmware.getVersion())
                    .description(firmware.getDescription())
                    .filePath(firmware.getFilePath())
                    .fileSize(firmware.getFileSize())
                    .status(firmware.getStatus() != null ? firmware.getStatus().toString() : null)
                    .createdAt(firmware.getCreatedAt())
                    .updatedAt(firmware.getUpdatedAt())
                    .build();

            return FirmwareResponse.success(metadata, "Firmware uploaded and saved successfully");

        } catch (Exception e) {
            return FirmwareResponse.error("UPLOAD_ERROR", "Failed to upload firmware", e.getMessage());
        }
    }


    public FirmwareResponse<FirmwareDownloadResponse> getFirmwareDownloadUrl(UUID deviceId) {

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        DeviceFirmwareRelation relation = deviceFirmwareRelationRepository
                .findTopByDeviceIdOrderByInstalledAtDesc(device.getId())
                .orElseThrow(() -> new RuntimeException("No firmware assigned to this device"));

        Firmware firmware = firmwareRepository.findById(relation.getFirmwareId())
                .orElseThrow(() -> new RuntimeException("Firmware not found"));

        if (!"RELEASED".equalsIgnoreCase(firmware.getStatus().toString())) {
            return FirmwareResponse.error("FIRMWARE_NOT_RELEASED",
                            "Firmware is not in RELEASED status", null);
        }
        // kiểm tra version xem lastest active
        String fileKey = firmware.getFilePath();

        if (!s3Service.exists(fileKey)) {
            return FirmwareResponse.error("FILE_NOT_FOUND",
                            "Firmware file not found on S3", null);
        }

        if (firmware.getChecksum() != null && !firmware.getChecksum().isBlank()) {
            boolean valid = s3Service.verifyFileChecksum(fileKey, firmware.getChecksum());
            if (!valid) {
                return FirmwareResponse.error("CHECKSUM_MISMATCH",
                                "Firmware checksum mismatch", null);
            }
        }

        String presignedUrl = s3Service.generatePresignedDownload(fileKey, 10);

        FirmwareDownloadHistory history = new FirmwareDownloadHistory();
        history.setDeviceId(device.getId());
        history.setFirmwareId(firmware.getId());
        history.setDownloadedAt(LocalDateTime.now());
        downloadHistoryRepository.save(history);

        FirmwareDownloadResponse response = new FirmwareDownloadResponse();
        response.setUrl(presignedUrl);

        return FirmwareResponse.success(response, "Firmware download URL generated");
    }



    public PagedResponse<FirmwareMetadataResponse> getFirmware(
            Integer page, Integer limit) {

        int pageIndex = (page != null && page > 0) ? page - 1 : 0;
        int pageSize = (limit != null && limit > 0) ? limit : 20;
        PageRequest pageable = PageRequest.of(pageIndex, pageSize);

        Page<Firmware> resultPage;

        resultPage = firmwareRepository.findAll(pageable);

        return PagedResponseHelper.build(resultPage, fw ->
                FirmwareMetadataResponse.builder()
                        .name(fw.getName())
                        .version(fw.getVersion())
                        .description(fw.getDescription())
                        .filePath(fw.getFilePath())
                        .fileSize(fw.getFileSize())
                        .checksum(fw.getChecksum())
                        .status(fw.getStatus() != null ? fw.getStatus().toString() : null)
                        .createdAt(fw.getCreatedAt())
                        .updatedAt(fw.getUpdatedAt())
                        .build()
        );
    }

    private String calculateSHA256(MultipartFile file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = file.getBytes();
        byte[] hashBytes = digest.digest(fileBytes);
        return HexFormat.of().formatHex(hashBytes);
    }

}
