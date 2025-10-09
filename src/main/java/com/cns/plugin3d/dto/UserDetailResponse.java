package com.cns.plugin3d.dto;

import com.cns.plugin3d.enums.StateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDetailResponse{
    private Long id;
    private String name;
    private String role;
    private String state;
    private String email;
}
