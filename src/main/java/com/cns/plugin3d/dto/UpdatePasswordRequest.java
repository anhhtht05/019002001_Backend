package com.cns.plugin3d.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordRequest {

    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    private String newPassword;

    @AssertTrue(message = "New password must be different from old password")
    public boolean isNewPasswordDifferent() {
        if (oldPassword == null || newPassword == null) {
            return true;
        }
        return !oldPassword.equals(newPassword);
    }
}