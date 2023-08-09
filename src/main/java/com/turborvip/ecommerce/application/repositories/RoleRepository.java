package com.turborvip.ecommerce.application.repositories;

import com.turborvip.ecommerce.application.constants.EnumRole;
import com.turborvip.ecommerce.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByRoleName(EnumRole roleName);
}
