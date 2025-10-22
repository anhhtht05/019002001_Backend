package com.cns.plugin3d.controller;

import com.cns.plugin3d.dto.*;
import com.cns.plugin3d.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public PagedResponse<UserDetailResponse> getUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "search", required = false) String search
    ) {
        return userService.getUsers(page, limit, role, state, search);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public CustomResponse addUser(@Valid @RequestBody AddUserRequest request) {
        return userService.addUser(request);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public UserDetailResponse updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return userService.updateUser(userId, request);
    }

}
