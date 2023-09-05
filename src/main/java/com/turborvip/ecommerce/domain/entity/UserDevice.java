package com.turborvip.ecommerce.domain.entity;

import com.turborvip.ecommerce.domain.entity.base.AbstractBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_device")
public class UserDevice extends AbstractBase {

    @NotEmpty(message = "Device id must not be empty")
    @Column(name = "device_id",unique = true)
    private String deviceID;

    @Column(name = "status")
    private String status;

    @NotEmpty(message = "Last login at must not be empty")
    @Column(name = "last_login_at")
    private Timestamp lastLoginAt;

    @Column(name = "locked_at")
    private Timestamp lockedAt;

    @Column(name = "locked_until")
    private Timestamp lockedUntil;

    @Column(name = "unlocked_at")
    private Timestamp unlockedAt;

}
