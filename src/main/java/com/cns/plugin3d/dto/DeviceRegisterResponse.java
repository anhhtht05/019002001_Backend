package com.cns.plugin3d.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceRegisterResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private ApiError error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiError {
        private String code;
        private String message;
        private String details;
    }
}
