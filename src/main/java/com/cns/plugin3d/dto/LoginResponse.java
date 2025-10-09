package com.cns.plugin3d.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String access_token;
    private String refresh_token;
    private long expires_in;
    private UserDTO user;
}