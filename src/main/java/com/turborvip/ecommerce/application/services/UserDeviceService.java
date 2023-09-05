package com.turborvip.ecommerce.application.services;

import com.turborvip.ecommerce.domain.entity.UserDevice;

import java.sql.Timestamp;
import java.util.Optional;

public interface UserDeviceService {
    Optional<UserDevice> findDeviceByUserIdAndDeviceId(Long userId,String deviceId);

    UserDevice create(UserDevice userDevice);

    UserDevice updateLastLogin(Timestamp lastLoginAt, Long userDeviceId) throws Exception;
}
