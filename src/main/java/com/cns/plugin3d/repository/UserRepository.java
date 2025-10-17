package com.cns.plugin3d.repository;


import com.cns.plugin3d.entity.User;
import com.cns.plugin3d.enums.RoleType;
import com.cns.plugin3d.enums.StateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Page<User> findByState(StateType state, Pageable pageable);
    Page<User> findByRole(RoleType roleType, Pageable pageable);
    Page<User> findByRoleAndState(RoleType roleType, StateType state, Pageable pageable);
}
