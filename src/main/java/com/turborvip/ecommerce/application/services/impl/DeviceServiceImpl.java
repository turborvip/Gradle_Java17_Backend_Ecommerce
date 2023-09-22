package com.turborvip.ecommerce.application.services.impl;

import com.turborvip.ecommerce.application.constants.CommonConstant;
import com.turborvip.ecommerce.application.constants.DevMessageConstant;
import com.turborvip.ecommerce.application.repositories.DeviceRepository;
import com.turborvip.ecommerce.application.services.DeviceService;
import com.turborvip.ecommerce.domain.entity.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    DeviceRepository deviceRepository;

    @Override
    public Device updateLastLogin(Timestamp lastLoginAt, Long deviceId) throws Exception {
        try {
            Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new Exception(DevMessageConstant.Common.EXIST_DEVICE));
            device.setLastLoginAt(lastLoginAt);
            return device;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw  e;
        }
    }

    @Override
    public Device findDeviceByUserAgent(String userAgent) {
        return deviceRepository.findByUserAgent(userAgent);
    }

    @Override
    public Device create(Device device) {
        return deviceRepository.save(device);
    }
}
