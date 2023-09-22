package com.turborvip.ecommerce.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class UserDeviceKey implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "device_id")
    private Long deviceId;
}
