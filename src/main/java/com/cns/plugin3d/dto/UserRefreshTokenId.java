package com.cns.plugin3d.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRefreshTokenId implements Serializable {
    private UUID user;
    private UUID refreshToken;
}
