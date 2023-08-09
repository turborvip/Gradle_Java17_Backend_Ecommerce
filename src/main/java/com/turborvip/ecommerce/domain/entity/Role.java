package com.turborvip.ecommerce.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.turborvip.ecommerce.application.constants.EnumRole;
import com.turborvip.ecommerce.domain.entity.base.AbstractBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@Table(name = "roles")
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role extends AbstractBase {

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    private EnumRole roleName;

    @ManyToMany(mappedBy = "roles", cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @Fetch(value = FetchMode.SELECT)
    @JsonInclude
    private Set<User> users = new HashSet<>();


    public Role(Long id, EnumRole enumRole) {
        this.id = id;
        this.roleName = EnumRole.valueOf(enumRole.name());
    }

    public Role(EnumRole enumRole) {
        this.roleName = EnumRole.valueOf(enumRole.name());
    }
}
