package com.turborvip.ecommerce.application.services.impl;

import com.turborvip.ecommerce.application.repositories.UserDeviceRepository;
import com.turborvip.ecommerce.application.repositories.UserRepository;
import com.turborvip.ecommerce.application.services.UserDeviceService;
import com.turborvip.ecommerce.domain.entity.UserDevice;
import com.turborvip.ecommerce.domain.entity.UserDeviceKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@Slf4j
public class UserDeviceServiceImpl implements UserDeviceService {
    @Autowired
    UserDeviceRepository userDeviceRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<UserDevice> findDeviceByUserIdAndDeviceId(Long userId, String deviceId) {
        return userDeviceRepository.findById_UserIdAndDevice_UserAgent(userId,deviceId);
    }

    @Override
    public Boolean checkUserDeviceByUserIdAndDeviceIdAndName(Long userId, String deviceId) {
        return null;
    }

    @Override
    public UserDevice create(UserDevice userDevice) {
        return userDeviceRepository.save(userDevice);
    }
}
