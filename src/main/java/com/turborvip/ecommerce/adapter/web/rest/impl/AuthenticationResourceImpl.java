package com.turborvip.ecommerce.adapter.web.rest.impl;

import com.turborvip.ecommerce.adapter.web.base.RestApiV1;
import com.turborvip.ecommerce.adapter.web.base.RestData;
import com.turborvip.ecommerce.adapter.web.base.VsResponseUtil;
import com.turborvip.ecommerce.adapter.web.rest.AuthenticationResource;
import com.turborvip.ecommerce.application.configuration.exception.BadRequestException;
import com.turborvip.ecommerce.application.https.request.AuthRequest;
import com.turborvip.ecommerce.application.https.response.AuthResponse;
import com.turborvip.ecommerce.application.https.response.RegisterResponse;
import com.turborvip.ecommerce.application.services.UserService;
import com.turborvip.ecommerce.application.services.impl.AuthService;
import com.turborvip.ecommerce.application.services.impl.JwtService;
import com.turborvip.ecommerce.domain.dto.Profile;
import com.turborvip.ecommerce.domain.dto.UserDTO;
import com.turborvip.ecommerce.domain.entity.Role;
import com.turborvip.ecommerce.domain.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.turborvip.ecommerce.application.constants.DevMessageConstant.Common.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.USER_AGENT;

@RestApiV1
@RequiredArgsConstructor
@Component
@Slf4j
public class AuthenticationResourceImpl implements AuthenticationResource {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public ResponseEntity<RestData<?>> login(AuthRequest authRequest, HttpServletRequest request) {
        try {
            return authService.authenticate(authRequest, request);
        } catch (Exception e) {
            return VsResponseUtil.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<RestData<?>> logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        try {
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, authentication);
            return authService.removeToken(request, response);
        } catch (Exception e) {
            return VsResponseUtil.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> testAuth() {
        return ResponseEntity.ok("Hi");
    }

    @Override
    public ResponseEntity<RestData<?>> refreshToken(HttpServletRequest request) {
        try {
            String DEVICE_ID = request.getHeader(USER_AGENT);
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            String token = authorizationHeader.substring("Bearer ".length());
            AuthResponse data = jwtService.generateTokenFromRefreshToken(token, DEVICE_ID);
            return VsResponseUtil.ok(REFRESH_TOKEN_SUCCESS, data);
        } catch (Exception exception) {
            return VsResponseUtil.error(HttpStatus.BAD_REQUEST, REFRESH_TOKEN_FAIL);
        }
    }

    @Override
    public ResponseEntity<RestData<?>> registerUser(UserDTO userDTO, HttpServletRequest request) {
        try {
            String DEVICE_ID = request.getHeader(USER_AGENT);
            User user = userService.registerUser(userDTO);
            List<String> listRole = new ArrayList<>();
            for (Role role : user.getRoles())
                listRole.add(String.valueOf(role.getRoleName()));
            String token = jwtService.generateToken(user, listRole, DEVICE_ID);
            String refreshToken = jwtService.generateRefreshToken(user, listRole, DEVICE_ID, null);
            Profile profile = new Profile(userDTO.getFullName(), user.getEmail(), userDTO.getBirthday(), userDTO.getGender(), userDTO.getPhone(), userDTO.getAddress(), userDTO.getAvatar());
            RegisterResponse registerResponse = new RegisterResponse(token, refreshToken, profile);
            return VsResponseUtil.ok(REGISTER_SUCCESS, registerResponse);
        } catch (Exception exception) {
            return VsResponseUtil.error(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }
}
