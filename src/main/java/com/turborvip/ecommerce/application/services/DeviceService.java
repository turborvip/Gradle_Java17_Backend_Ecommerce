package com.turborvip.ecommerce.application.services;

import com.turborvip.ecommerce.domain.entity.Device;

import java.sql.Timestamp;
import java.util.Optional;

public interface DeviceService {
    Device updateLastLogin(Timestamp lastLoginAt, Long deviceId) throws Exception;

    Device findDeviceByUserAgent(String userAgent);

    Device create(Device device);

}
