package com.cns.plugin3d.service;


import com.cns.plugin3d.dto.*;
import com.cns.plugin3d.entity.User;
import com.cns.plugin3d.enums.RoleType;
import com.cns.plugin3d.enums.StateType;
import com.cns.plugin3d.exception.CustomException;
import com.cns.plugin3d.helper.PagedResponseHelper;
import com.cns.plugin3d.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PagedResponse<UserDetailResponse> getUsers (
            Integer page, Integer limit, String role, String state, String search){
        int pageIndex = (page != null && page > 0) ? page - 1 : 0;
        int pageSize = (limit != null && limit > 0) ? limit : 20;
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        Page<User> resultPage = userRepository.findFilteredUsers(
                role != null ? role.toUpperCase() : null,
                state != null ? state.toUpperCase() : null,
                (search != null && !search.isBlank()) ? search : null,
                pageable
        );

        return PagedResponseHelper.build(resultPage, user -> UserDetailResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .state(user.getState().name())
                .build()
        );
    }

    public UserDetailResponse updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        if (request.getState() != null) {
            user.setState(request.getState());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        userRepository.save(user);

        return UserDetailResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .state(user.getState().name())
                .build();
    }
    public CustomResponse addUser(AddUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException("Email already exists", HttpStatus.CONFLICT);
        }

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(RoleType.ROLE_ADMIN);
        newUser.setState(StateType.ACTIVE);

        userRepository.save(newUser);

        return new CustomResponse(
                true,
                "Add user successfully",
                Instant.now()
        );
    }


}
