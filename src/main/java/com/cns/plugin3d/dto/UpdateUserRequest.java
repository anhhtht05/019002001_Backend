package com.cns.plugin3d.dto;

import com.cns.plugin3d.enums.RoleType;
import com.cns.plugin3d.enums.StateType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    private StateType state;
    private RoleType role;
}
