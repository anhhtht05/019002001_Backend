package com.cns.plugin3d.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRefreshTokenId implements java.io.Serializable {
    private UUID user;
    private UUID refreshToken;
}
