package com.cns.plugin3d.entity;

import com.cns.plugin3d.dto.UserRefreshTokenId;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_refresh_tokens")
@IdClass(UserRefreshTokenId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRefreshToken {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "refresh_token_id", nullable = false)
    private RefreshToken refreshToken;

}

