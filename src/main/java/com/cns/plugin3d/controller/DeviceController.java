package com.cns.plugin3d.controller;

import com.cns.plugin3d.dto.*;
import com.cns.plugin3d.service.DeviceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping("/register")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public DeviceRegisterResponse<DeviceRegisterDetailResponse> registerDevice(
            @Valid @RequestBody DeviceRegisterRequest request) {
        return deviceService.register(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public PagedResponse<DeviceResponse> getDevices(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(name = "device_type", required = false) String deviceType,
            @RequestParam(name = "hardware", required = false) String hardware,
            @RequestParam(name = "model", required = false) String model,
            @RequestParam(name = "search", required = false) String search
    ) {
        return deviceService.getDevice(page, limit, deviceType, hardware, model, search);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public DeviceRegisterResponse<DeviceResponse> updateDevice(@Valid @RequestBody DeviceUpdateRequest request) {
        return deviceService.updateDevice(request);
    }

    @DeleteMapping("/{deviceId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public CustomResponse deleteDevice(@PathVariable String deviceId) {
        return deviceService.deleteDevice(deviceId);
    }

}
