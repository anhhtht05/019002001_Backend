package com.cns.plugin3d.service;


import com.cns.plugin3d.dto.PagedResponse;
import com.cns.plugin3d.dto.UpdateUserStateRequest;
import com.cns.plugin3d.dto.UserDTO;
import com.cns.plugin3d.dto.UserDetailResponse;
import com.cns.plugin3d.entity.User;
import com.cns.plugin3d.enums.RoleType;
import com.cns.plugin3d.enums.ServiceType;
import com.cns.plugin3d.enums.StateType;
import com.cns.plugin3d.exception.CustomException;
import com.cns.plugin3d.helper.PagedResponseHelper;
import com.cns.plugin3d.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public PagedResponse<UserDetailResponse> getUsers (
            Integer page, Integer limit, String role, String state){
        int pageIndex = (page != null && page > 0) ? page - 1 : 0;
        int pageSize = (limit != null && limit > 0) ? limit : 20;
        PageRequest pageable = PageRequest.of(pageIndex, pageSize);

        Page<User> resultPage;
        if (role != null && state != null) {
            RoleType st = RoleType.valueOf(role.toUpperCase());
            StateType stt = StateType.valueOf(state.toUpperCase());
            resultPage = userRepository.findByRoleAndState(st, stt, pageable);
        } else if (role != null) {
            RoleType st = RoleType.valueOf(role.toUpperCase());
            resultPage = userRepository.findByRole(st, pageable);
        } else if (state != null) {
            StateType stt = StateType.valueOf(state.toUpperCase());
            resultPage = userRepository.findByState(stt, pageable);
        } else {
            resultPage = userRepository.findAll(pageable);
        }

        return PagedResponseHelper.build(resultPage, user -> UserDetailResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .state(user.getState().name())
                .build()
        );
    }

    public UserDetailResponse updateUser(Long userId, UpdateUserStateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        user.setState(request.getState());
        userRepository.save(user);

        return UserDetailResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .state(user.getState().name())
                .build();
    }
}
