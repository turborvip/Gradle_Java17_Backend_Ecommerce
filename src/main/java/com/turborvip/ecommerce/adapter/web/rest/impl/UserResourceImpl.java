package com.turborvip.ecommerce.adapter.web.rest.impl;

import com.turborvip.ecommerce.adapter.web.base.RestApiV1;
import com.turborvip.ecommerce.adapter.web.base.VsResponseUtil;
import com.turborvip.ecommerce.adapter.web.rest.UserResource;
import com.turborvip.ecommerce.application.services.UserService;
import com.turborvip.ecommerce.domain.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@RestApiV1
@RequiredArgsConstructor
@Component("UserResourceImpl")
public class UserResourceImpl implements UserResource {
    private UserService userService;

    @Override
    public ResponseEntity<?> create(User user, HttpServletRequest request) {
        return VsResponseUtil.ok(null, userService.create(user, request), null);
    }
}
