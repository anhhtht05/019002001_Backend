package com.cns.plugin3d.dto;

import com.cns.plugin3d.enums.StateType;
import lombok.Data;

@Data
public class UpdateUserStateRequest {
    private StateType state;
}
