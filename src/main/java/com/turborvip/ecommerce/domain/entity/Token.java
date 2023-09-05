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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tokens", schema = "token")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Token extends AbstractBase {
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @NotEmpty(message = "Type must not be empty")
    @Column(name = "type")
    private String type;

    @Column(name = "value",columnDefinition="TEXT")
    private String value;

    @NotEmpty(message = "Verify key must not be empty")
    @Column(name="verify_key",columnDefinition="TEXT")
    private  String verifyKey;

    @Column(name="refresh_token_used",columnDefinition="TEXT[]")
    private ArrayList<String> refreshTokenUsed;

    @NotEmpty(message = "Expire time must not be empty")
    @Column(name = "expires_at")
    private Timestamp expiresAt;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "user_device_id",referencedColumnName = "device_id")
    private UserDevice userDevices;
}
