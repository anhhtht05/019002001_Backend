package com.cns.plugin3d.service;

import com.cns.plugin3d.dto.*;
import com.cns.plugin3d.entity.*;
import com.cns.plugin3d.enums.InstallationStatusType;
import com.cns.plugin3d.enums.StatusType;
import com.cns.plugin3d.exception.CustomException;
import com.cns.plugin3d.helper.PagedResponseHelper;
import com.cns.plugin3d.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FirmwareService {

    private final FirmwareRepository firmwareRepository;
    private final S3Service s3Service;
    private final DeviceRepository deviceRepository;
    private final FirmwareDownloadHistoryRepository firmwareDownloadHistoryRepository;
    private final DeviceFirmwareRelationRepository deviceFirmwareRelationRepository;
    private final FirmwareModelCompatibilityRepository firmwareModelCompatibilityRepository;
    private final FirmwareHardwareCompatibilityRepository firmwareHardwareCompatibilityRepository;
    private final DeviceCredentialsRepository deviceCredentialRepository;

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
            String formattedName = request.getFirmwareName()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9\\s_-]", "")
                    .replaceAll("\\s+", "_");

            String key = String.format("%s/%s/%s_%s.bin",
                    s3Service.getFirmwarePrefix(),
                    request.getVersion(),
                    formattedName,
                    request.getVersion());

//            System.out.println(request.getHardwareCompat());
//            System.out.println(request.getModelCompat());
//            String key = String.format("%s/%s/%s",
//                    s3Service.getFirmwarePrefix(),
//                    request.getVersion(),
//                    request.getFile().getOriginalFilename());

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
            Firmware savedFirmware = firmwareRepository.save(firmware);

            for (String modelName : request.getModelCompat()) {
                System.out.println("modelName" +modelName);
                FirmwareModelCompatibility model = new FirmwareModelCompatibility();
                model.setFirmwareId(savedFirmware.getId());
                model.setModel(modelName);
                firmwareModelCompatibilityRepository.save(model);
            }

            for (String hardwareVer : request.getHardwareCompat()) {
                System.out.println("hardwareVer" +hardwareVer);
                FirmwareHardwareCompatibility hardware = new FirmwareHardwareCompatibility();
                hardware.setFirmwareId(savedFirmware.getId());
                hardware.setHardwareVersion(hardwareVer);
                firmwareHardwareCompatibilityRepository.save(hardware);
            }

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


    public FirmwareResponse<FirmwareDownloadResponse> getFirmwareDownloadUrl(String token, String macAddress) {

        Device device = deviceRepository.findByMacAddress(macAddress)
                .orElseThrow(() -> new CustomException("DEVICE_NOT_FOUND", HttpStatus.NOT_FOUND));

        DeviceCredential credential = deviceCredentialRepository.findByDeviceId(device.getId())
                .orElseThrow(() -> new CustomException("DEVICE_CREDENTIAL_NOT_FOUND", HttpStatus.UNAUTHORIZED));

        if (!credential.getApiKey().equals(token)) {
            throw new CustomException("INVALID_TOKEN", HttpStatus.UNAUTHORIZED);
        }

        // FIND COMPATIBLE FIRMWARE (latest RELEASED)
        Firmware firmware = firmwareRepository
                .findLatestReleasedFirmwareByModelAndHardware(
                        device.getModel(), device.getHardwareVersion(), StatusType.RELEASED)
                .orElseThrow(() -> new CustomException("NO_COMPATIBLE_FIRMWARE", HttpStatus.NOT_FOUND));

        // GENERATE PRE-SIGNED URL
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

        // SAVE HISTORY
        FirmwareDownloadHistory history = FirmwareDownloadHistory.builder()
                .deviceId(device.getId())
                .firmwareId(firmware.getId())
                .downloadedAt(LocalDateTime.now())
                .build();
        firmwareDownloadHistoryRepository.save(history);

        //  SAVE RELATION
        Optional<DeviceFirmwareRelation> existingRelation =
                deviceFirmwareRelationRepository.findByDeviceIdAndFirmwareId(device.getId(), firmware.getId());

        if (existingRelation.isEmpty()) {
            DeviceFirmwareRelation relation = DeviceFirmwareRelation.builder()
                    .deviceId(device.getId())
                    .firmwareId(firmware.getId())
                    .installedAt(LocalDateTime.now())
                    .installationStatus(InstallationStatusType.PENDING)
                    .build();

            deviceFirmwareRelationRepository.save(relation);
        }
        FirmwareDownloadResponse response = new FirmwareDownloadResponse();
        response.setUrl(presignedUrl);

        return FirmwareResponse.success(response, "Firmware download URL generated");
    }

    public PagedResponse<FirmwareMetadataResponse> getFirmware (
            Integer page, Integer limit ) {

        int pageIndex = (page != null && page > 0) ? page - 1 : 0;
        int pageSize = (limit != null && limit > 0) ? limit : 20;
        PageRequest pageable = PageRequest.of(pageIndex, pageSize);

        Page<Firmware> resultPage;

        resultPage = firmwareRepository.findAll(pageable);

        return PagedResponseHelper.build(resultPage, fw -> {
            List<String> models = firmwareModelCompatibilityRepository.findModelByFirmwareId(fw.getId());
            List<String> hardwares = firmwareHardwareCompatibilityRepository.findHardwareByFirmwareId(fw.getId());
            return FirmwareMetadataResponse.builder()
                    .id(fw.getId().toString())
                    .name(fw.getName())
                    .version(fw.getVersion())
                    .description(fw.getDescription())
                    .filePath(fw.getFilePath())
                    .fileSize(fw.getFileSize())
                    .status(fw.getStatus() != null ? fw.getStatus().toString() : null)
                    .createdAt(fw.getCreatedAt())
                    .updatedAt(fw.getUpdatedAt())
                    .modelCompat(models)
                    .hardwareCompat(hardwares)
                    .build();
        });
    }

    @Transactional
    public FirmwareResponse<FirmwareMetadataResponse> updateFirmware(String firmwareId, FirmwareUpdateRequest request) {
        try {
            UUID id;
            try {
                id = UUID.fromString(firmwareId);
            } catch (IllegalArgumentException e) {
                return FirmwareResponse.error("INVALID_ID", "Invalid firmware ID format", null);
            }

            Firmware firmware = firmwareRepository.findById(id)
                    .orElse(null);

            if (firmware == null) {
                return FirmwareResponse.error("NOT_FOUND", "Firmware not found", null);
            }

            if (firmware.getStatus() == StatusType.RELEASED) {
                return FirmwareResponse.error(
                        "UPDATE_FORBIDDEN",
                        "Firmware in RELEASED state cannot be updated",
                        null
                );
            }

            firmware.setDescription(request.getDescription());
            firmware.setStatus(request.getStatus());
            firmware.setUpdatedAt(LocalDateTime.now());
            firmwareRepository.save(firmware);

            firmwareModelCompatibilityRepository.deleteByFirmwareId(firmware.getId());
            firmwareModelCompatibilityRepository.flush();

            firmwareHardwareCompatibilityRepository.deleteByFirmwareId(firmware.getId());
            firmwareHardwareCompatibilityRepository.flush();

            for (String model : request.getModelCompat()) {
                System.out.println(model);

                FirmwareModelCompatibility compat = new FirmwareModelCompatibility();
                compat.setFirmwareId(firmware.getId());
                compat.setModel(model);
                firmwareModelCompatibilityRepository.save(compat);
            }

            for (String hw : request.getHardwareCompat()) {
                System.out.println(hw);
                FirmwareHardwareCompatibility compat = new FirmwareHardwareCompatibility();
                compat.setFirmwareId(firmware.getId());
                compat.setHardwareVersion(hw);
                firmwareHardwareCompatibilityRepository.save(compat);
            }
            List<String> models = firmwareModelCompatibilityRepository.findModelByFirmwareId(firmware.getId());
            List<String> hardwares = firmwareHardwareCompatibilityRepository.findHardwareByFirmwareId(firmware.getId());
            System.out.println(models);
            System.out.println(hardwares);

            FirmwareMetadataResponse metadata = FirmwareMetadataResponse.builder()
                    .name(firmware.getName())
                    .version(firmware.getVersion())
                    .description(firmware.getDescription())
                    .filePath(firmware.getFilePath())
                    .fileSize(firmware.getFileSize())
                    .modelCompat(models)
                    .hardwareCompat(hardwares)
                    .status(firmware.getStatus() != null ? firmware.getStatus().toString() : null)
                    .createdAt(firmware.getCreatedAt())
                    .updatedAt(firmware.getUpdatedAt())
                    .build();

            return FirmwareResponse.success(metadata, "Firmware uploaded and saved successfully");

        } catch (Exception e) {
            return FirmwareResponse.error("UPDATE_ERROR", "Failed to update firmware", e.getMessage());
        }
    }

    private String calculateSHA256(MultipartFile file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = file.getBytes();
        byte[] hashBytes = digest.digest(fileBytes);
        return HexFormat.of().formatHex(hashBytes);
    }

//    public FirmwareResponse<FirmwareDownloadResponse> getFirmwareDownloadUrl(UUID deviceId) {
//
//        Device device = deviceRepository.findById(deviceId)
//                .orElseThrow(() -> new RuntimeException("Device not found"));
//
//        DeviceFirmwareRelation relation = deviceFirmwareRelationRepository
//                .findTopByDeviceIdOrderByInstalledAtDesc(device.getId())
//                .orElseThrow(() -> new RuntimeException("No firmware assigned to this device"));
//
//        Firmware firmware = firmwareRepository.findById(relation.getFirmwareId())
//                .orElseThrow(() -> new RuntimeException("Firmware not found"));
//
//        if (!"RELEASED".equalsIgnoreCase(firmware.getStatus().toString())) {
//            return FirmwareResponse.error("FIRMWARE_NOT_RELEASED",
//                    "Firmware is not in RELEASED status", null);
//        }
//        // kiểm tra version xem lastest active
//        String fileKey = firmware.getFilePath();
//
//        if (!s3Service.exists(fileKey)) {
//            return FirmwareResponse.error("FILE_NOT_FOUND",
//                    "Firmware file not found on S3", null);
//        }
//
//        if (firmware.getChecksum() != null && !firmware.getChecksum().isBlank()) {
//            boolean valid = s3Service.verifyFileChecksum(fileKey, firmware.getChecksum());
//            if (!valid) {
//                return FirmwareResponse.error("CHECKSUM_MISMATCH",
//                        "Firmware checksum mismatch", null);
//            }
//        }
//
//        String presignedUrl = s3Service.generatePresignedDownload(fileKey, 10);
//
//        FirmwareDownloadHistory history = new FirmwareDownloadHistory();
//        history.setDeviceId(device.getId());
//        history.setFirmwareId(firmware.getId());
//        history.setDownloadedAt(LocalDateTime.now());
//        downloadHistoryRepository.save(history);
//
//        FirmwareDownloadResponse response = new FirmwareDownloadResponse();
//        response.setUrl(presignedUrl);
//
//        return FirmwareResponse.success(response, "Firmware download URL generated");
//    }

}
