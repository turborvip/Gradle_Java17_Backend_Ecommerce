package com.turborvip.ecommerce.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.turborvip.ecommerce.domain.entity.base.AbstractBase;
import jakarta.persistence.*;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "devices",schema = "account")
public class Device extends AbstractBase {
    @NotEmpty(message = "Device id must not be empty")
    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "ip")
    private String ip;

    @Column(name = "channel")
    private String channel;

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

//    @OneToMany(mappedBy = "device")
//    private Set<UserDevice> users;
}
