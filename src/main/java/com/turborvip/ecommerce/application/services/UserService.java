package com.turborvip.ecommerce.application.services;

import com.turborvip.ecommerce.domain.dto.UserDTO;
import com.turborvip.ecommerce.domain.entity.Role;
import com.turborvip.ecommerce.domain.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;


public interface UserService {
    User create(User user, HttpServletRequest request);

    User registerUser(UserDTO userDTO);

    Optional<User> findById(Long id);

    Role saveRole(Role role);

    void addToUser(String username, String role_name);
}
