package com.cns.plugin3d.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.net.InetAddress;

@Converter
public class InetAddressConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        // PostgreSQL inet chấp nhận string IP chuẩn
        return attribute;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData;
    }
}
