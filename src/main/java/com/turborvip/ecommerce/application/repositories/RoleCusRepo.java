package com.turborvip.ecommerce.application.repositories;

import com.turborvip.ecommerce.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleCusRepo extends JpaRepository<Role, Long> {
     List<Role> findByUsers_Username(String username);

     List<Role> findByUsers_Id(Long id);
}