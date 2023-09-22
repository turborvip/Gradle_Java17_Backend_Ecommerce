package com.turborvip.ecommerce.application.repositories;

import com.turborvip.ecommerce.domain.entity.Device;
import com.turborvip.ecommerce.domain.entity.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository  extends JpaRepository<Device, Long> {

    @Override
    @NotNull
    Optional<Device> findById(@NotNull Long id);

    Device findByUserAgent(String userAgent);


}
