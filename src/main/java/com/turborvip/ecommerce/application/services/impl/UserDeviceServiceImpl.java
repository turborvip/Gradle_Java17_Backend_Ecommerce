package com.turborvip.ecommerce.application.services.impl;

import com.turborvip.ecommerce.application.repositories.UserDeviceRepository;
import com.turborvip.ecommerce.application.repositories.UserRepository;
import com.turborvip.ecommerce.application.services.UserDeviceService;
import com.turborvip.ecommerce.application.services.UserService;
import com.turborvip.ecommerce.domain.entity.User;
import com.turborvip.ecommerce.domain.entity.UserDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class UserDeviceServiceImpl implements UserDeviceService {
    @Autowired
    UserDeviceRepository userDeviceRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<UserDevice> findDeviceByUserIdAndDeviceId(Long userId,String deviceId) {
        return userDeviceRepository.findFirstByCreateBy_IdAndDeviceID(userId,deviceId);
    }

    @Override
    public UserDevice create(UserDevice userDevice) {
        return userDeviceRepository.save(userDevice);
    }

    @Override
    public UserDevice updateLastLogin(Timestamp lastLoginAt, Long userDeviceId) throws Exception {
        UserDevice userDevice = userDeviceRepository.findById(userDeviceId).orElseThrow(() -> new Exception("Don't have user device by id!"));
        userDevice.setLastLoginAt(lastLoginAt);
        return userDeviceRepository.save(userDevice);
    }
}
