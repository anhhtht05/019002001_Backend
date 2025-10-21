package com.cns.plugin3d.repository;


import com.cns.plugin3d.entity.User;
import com.cns.plugin3d.enums.RoleType;
import com.cns.plugin3d.enums.StateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Page<User> findByState(StateType state, Pageable pageable);
    Page<User> findByRole(RoleType roleType, Pageable pageable);
    Page<User> findByRoleAndState(RoleType roleType, StateType state, Pageable pageable);

    @Query(value = """
        SELECT *
        FROM users u
        WHERE (:role IS NULL OR u.role = :role)
          AND (:state IS NULL OR u.state = :state)
          AND (:search IS NULL OR (
                LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
             OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
          ))
        ORDER BY u.created_at DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM users u
        WHERE (:role IS NULL OR u.role = :role)
          AND (:state IS NULL OR u.state = :state)
          AND (:search IS NULL OR (
                LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
             OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
          ))
        """,
            nativeQuery = true)
    Page<User> findFilteredUsers(
            @Param("role") String role,
            @Param("state") String state,
            @Param("search") String search,
            Pageable pageable
    );


    @Query(value = """
    SELECT *
    FROM users u
    WHERE (:role IS NULL OR u.role = :role)
      AND (:state IS NULL OR u.state = :state)
      AND (:search IS NULL OR (
            LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
      ))
    ORDER BY u.created_at DESC
    """,
            nativeQuery = true)
    List<User> findFilteredUsersList(
            @Param("role") String role,
            @Param("state") String state,
            @Param("search") String search
    );

}
