package com.cns.plugin3d.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StateType {
    ACTIVE,
    INACTIVE,
    DELETED;

    @JsonCreator
    public static StateType from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return StateType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid StateType: " + value);
        }
    }

}
