package com.turborvip.ecommerce.adapter.web.rest;

import com.turborvip.ecommerce.adapter.web.base.RestData;
import com.turborvip.ecommerce.application.https.request.AuthRequest;
import com.turborvip.ecommerce.domain.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface AuthenticationResource {
    @PostMapping("/login")
    ResponseEntity<RestData<?>> login(@RequestBody AuthRequest authRequest, HttpServletRequest request) throws NoSuchAlgorithmException;

    @PostMapping("/logout")
    ResponseEntity<RestData<?>> logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) throws IOException;

    @PostMapping("/auth/test")
    ResponseEntity<String> testAuth();

    @PostMapping("/auth/refresh-token")
    ResponseEntity<RestData<?>> refreshToken(HttpServletRequest request);

    @PostMapping("/both/register")
    ResponseEntity<RestData<?>> registerUser(@RequestBody UserDTO userDTO,HttpServletRequest request);
}
