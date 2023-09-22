package com.turborvip.ecommerce.application.repositories;

import com.turborvip.ecommerce.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RoleCusRepo extends JpaRepository<Role, Long> {
     Set<Role> findByUsers_Username(String username);
}
