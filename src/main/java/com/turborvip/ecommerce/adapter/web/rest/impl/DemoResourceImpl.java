package com.turborvip.ecommerce.adapter.web.rest.impl;

import com.turborvip.ecommerce.adapter.web.base.RestApiV1;
import com.turborvip.ecommerce.adapter.web.base.RestData;
import com.turborvip.ecommerce.adapter.web.base.VsResponseUtil;
import com.turborvip.ecommerce.adapter.web.rest.DemoResource;
import com.turborvip.ecommerce.application.configuration.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static com.turborvip.ecommerce.application.constants.DevMessageConstant.Common.OBJECT_IS_EMPTY;


@RestApiV1
@Component("DemoResourceImpl")
public class DemoResourceImpl implements DemoResource {

    @Override
    public ResponseEntity<?> test(HttpServletRequest request) {
        return ResponseEntity.ok("Server is running!");
    }

    @Override
    public ResponseEntity<?> demoAuth(HttpServletRequest request) {
        return ResponseEntity.ok("Authentication & Authorization is successfully!");
    }

    @Override
    public void demoThrowException(HttpServletRequest request) {
        throw new BadRequestException("oke");
    }

    @Override
    public ResponseEntity<RestData<?>> demoSendData(HttpServletRequest request) {
        HashMap<String, String> data = new HashMap<>();
        data.put("ok", "myFriend");
        return VsResponseUtil.ok(OBJECT_IS_EMPTY, data, "Hi");
    }
}
