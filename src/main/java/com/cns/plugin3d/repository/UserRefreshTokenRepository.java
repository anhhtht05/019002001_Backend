package com.cns.plugin3d.repository;

import com.cns.plugin3d.dto.UserRefreshTokenId;
import com.cns.plugin3d.entity.User;
import com.cns.plugin3d.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, UserRefreshTokenId> {

    @Query("SELECT u FROM UserRefreshToken urt JOIN urt.user u WHERE urt.refreshToken.id = :refreshTokenId")
    Optional<User> findUserByRefreshToken(@Param("refreshTokenId") UUID refreshTokenId);

}
