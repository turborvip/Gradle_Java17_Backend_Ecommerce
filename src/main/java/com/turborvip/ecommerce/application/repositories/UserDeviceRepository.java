package com.turborvip.ecommerce.application.repositories;

import com.turborvip.ecommerce.domain.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    Optional<UserDevice> findById_UserIdAndDevice_UserAgent(Long userId, String userAgent);



}
