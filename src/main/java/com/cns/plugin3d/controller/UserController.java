package com.cns.plugin3d.controller;

import com.cns.plugin3d.dto.PagedResponse;
import com.cns.plugin3d.dto.UpdatePasswordRequest;
import com.cns.plugin3d.dto.UpdateUserStateRequest;
import com.cns.plugin3d.dto.UserDetailResponse;
import com.cns.plugin3d.entity.User;
import com.cns.plugin3d.enums.StateType;
import com.cns.plugin3d.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public PagedResponse<UserDetailResponse> getUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "state", required = false) String state
    ) {
        return userService.getUsers(page, limit, role, state);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public UserDetailResponse updateUser(
            @PathVariable UUID userId,
            @RequestBody UpdateUserStateRequest request
    ) {
        return userService.updateUser(userId, request);
    }




}
