package com.turborvip.ecommerce.application.repositories;

import com.turborvip.ecommerce.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByUsername(String email);

    Optional<User> findByUsername(String username);
}
