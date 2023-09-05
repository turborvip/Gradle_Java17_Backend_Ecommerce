package com.turborvip.ecommerce.application.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turborvip.ecommerce.adapter.web.base.RestData;
import com.turborvip.ecommerce.adapter.web.base.VsResponseUtil;
import com.turborvip.ecommerce.application.constants.DevMessageConstant;
import com.turborvip.ecommerce.application.constants.EnumRole;
import com.turborvip.ecommerce.application.repositories.RoleCusRepo;
import com.turborvip.ecommerce.application.repositories.TokenRepository;
import com.turborvip.ecommerce.application.repositories.UserRepository;
import com.turborvip.ecommerce.application.https.request.AuthRequest;
import com.turborvip.ecommerce.application.https.response.AuthResponse;
import com.turborvip.ecommerce.application.services.TokenService;
import com.turborvip.ecommerce.domain.entity.Role;
import com.turborvip.ecommerce.domain.entity.Token;
import com.turborvip.ecommerce.domain.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.turborvip.ecommerce.application.constants.DevMessageConstant.Common.LOGIN_SUCCESS;
import static com.turborvip.ecommerce.application.constants.DevMessageConstant.Common.LOGOUT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.USER_AGENT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    @Autowired
    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    @Autowired
    private final RoleCusRepo roleCusRepo;

    private final JwtService jwtService;
    private final TokenService tokenService;

    @Autowired
    private final TokenRepository tokenRepository;

    public ResponseEntity<RestData<?>> authenticate(AuthRequest authRequest, HttpServletRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            User user = null;
            if(authRequest.getUsername() !=  null) {
                user = userRepository.findByUsername(authRequest.getUsername()).orElse(null);
            }

            Set<Role> roleDB = null;
            if (user != null) {
                roleDB = roleCusRepo.findByUsers_Username(user.getUsername());
            }

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            assert roleDB != null;
            user.setRoles(roleDB);
            roleDB.forEach(i -> authorities.add(new SimpleGrantedAuthority(i.getRoleName().toString())));

            String DEVICE_ID = request.getHeader(USER_AGENT);
            List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).toList();
            var jwtToken = jwtService.generateToken(user, roles, DEVICE_ID);
            var jwtRefreshToken = jwtService.generateRefreshToken(user, roles, DEVICE_ID, null);
            AuthResponse authResponse = new AuthResponse(jwtToken,jwtRefreshToken);
            return VsResponseUtil.ok(LOGIN_SUCCESS, authResponse);
        } catch (Exception err) {
            log.warn(err.getMessage());
            return VsResponseUtil.error(FORBIDDEN, DevMessageConstant.Common.LOGIN_FAIL);
        }
    }

    public ResponseEntity<RestData<?>> removeToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String DEVICE_ID = request.getHeader(USER_AGENT);
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
                    Token tokenExist = tokenService.findFirstTokenByValue(token).orElse(null);
                    if (tokenExist != null) {
                        List<Token> listToken = tokenService.findListTokenByUserAndDevice(tokenExist.getCreateBy().getId(), DEVICE_ID);
                        listToken.forEach(tokenRepository::delete);
                    }
                } catch (Exception exception) {
                    response.setHeader("error", exception.getMessage());
                    response.setStatus(FORBIDDEN.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error_message", exception.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            }
            return VsResponseUtil.ok(LOGOUT,null);

        } catch (Exception ignored) {
            return VsResponseUtil.error(HttpStatus.BAD_REQUEST,null);
        }
    }


}
