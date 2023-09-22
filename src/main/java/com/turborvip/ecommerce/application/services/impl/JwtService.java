package com.turborvip.ecommerce.application.services.impl;

import com.turborvip.ecommerce.application.configuration.EcommerceProperties;
import com.turborvip.ecommerce.application.https.response.AuthResponse;
import com.turborvip.ecommerce.application.services.DeviceService;
import com.turborvip.ecommerce.application.services.TokenService;
import com.turborvip.ecommerce.application.services.UserDeviceService;
import com.turborvip.ecommerce.domain.entity.*;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.*;
import java.sql.Timestamp;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Autowired
    EcommerceProperties ecommerceProperties;

    @Autowired
    private final TokenService tokenService;

    @Autowired
    private final UserDeviceService userDeviceService;

    @Autowired
    private final DeviceService deviceService;

    public String generateToken(User user, List<String> roles, String DEVICE_ID) throws Exception {
        try {
            Timestamp now = new Timestamp(System.currentTimeMillis());

            // TODO check device
            Device device = deviceService.findDeviceByUserAgent(DEVICE_ID);
            if (device == null) {
                device = deviceService.create(new Device(DEVICE_ID, null, null, "active", now, null, null, null));
            }
            // TODO check user device
            UserDevice userDevice = userDeviceService.findDeviceByUserIdAndDeviceId(user.getId(), DEVICE_ID).orElse(null);
            if (userDevice != null) {
                deviceService.updateLastLogin(now, userDevice.getDevice().getId());
            } else {
                // create a section...
                userDevice = userDeviceService.create(new UserDevice(new UserDeviceKey(user.getId(), device.getId()), user, device));
            }

            // TODO generate secret key
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(4096);
            KeyPair pair = keyPairGen.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());

            // TODO generate jwt
            Timestamp expiredTime = new Timestamp(System.currentTimeMillis() + ecommerceProperties.getAccessTokenDueTime() * 1000L);
            String jwtGenerate = this.generateTokenUtil(user.getUsername(), roles, privateKey, expiredTime);

            // TODO update or create token
            Token tokenExisted = tokenService.findFirstTokenByUserIdAndNameAndDeviceId(user.getId(), "Access", DEVICE_ID).orElse(null);
            if (tokenExisted != null) {
                // update value, publicKey, expiredTime, updateAt
                tokenService.updateTokenWithValueExpiredTime(tokenExisted, now, jwtGenerate, expiredTime, publicKeyString, null);
            } else {
                Token token = new Token("Access", null, "Bear", jwtGenerate, publicKeyString, new ArrayList<>(), expiredTime, userDevice);
                token.setCreateBy(user);
                token.setUpdateBy(user);
                tokenService.create(token);
            }
            return jwtGenerate;

        } catch (Exception exception) {
            log.error("generate access Token fail! " + exception.getMessage());
            throw exception;
        }
    }

    public String generateRefreshToken(User user, List<String> roles, String DEVICE_ID, String refreshToken) throws Exception {
        try {
            Timestamp now = new Timestamp(System.currentTimeMillis());

            // TODO check device
            Device device = deviceService.findDeviceByUserAgent(DEVICE_ID);
            if (device == null) {
                device = deviceService.create(new Device(DEVICE_ID, null, null, "active", now, null, null, null));
            }

            // TODO check user device
            UserDevice userDevice = userDeviceService.findDeviceByUserIdAndDeviceId(user.getId(), DEVICE_ID).orElse(null);
            if (userDevice != null) {
                deviceService.updateLastLogin(now, userDevice.getDevice().getId());
            } else {
                userDevice = userDeviceService.create(new UserDevice(new UserDeviceKey(user.getId(), device.getId()), user, device));
            }

            // TODO generate secret key
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(4096);
            KeyPair pair = keyPairGen.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());

            // TODO generate jwt
            Timestamp expiredTime = new Timestamp(System.currentTimeMillis() + ecommerceProperties.getRefreshTokenDueTime() * 1000L);
            String jwtGenerate = this.generateTokenUtil(user.getUsername(), roles, privateKey, expiredTime);

            // TODO check token
            Token tokenExisted = tokenService.findFirstTokenByUserIdAndNameAndDeviceId(user.getId(), "Refresh", DEVICE_ID).orElse(null);
            if (tokenExisted != null) {
                // update value, publicKey, expiredTime, updateAt
                tokenService.updateTokenWithValueExpiredTime(tokenExisted, now, jwtGenerate, expiredTime, publicKeyString, refreshToken);
            } else {
                Token token = new Token("Refresh", null, "Bear", jwtGenerate, publicKeyString, new ArrayList<>(), expiredTime, userDevice);
                token.setCreateBy(user);
                token.setUpdateBy(user);
                tokenService.create(token);
            }
            return jwtGenerate;

        } catch (Exception exception) {
            log.error("generate refreshToken fail!");
            throw exception;
        }
    }

    public String generateTokenUtil(String username, List<String> roles, PrivateKey privateKey, Timestamp expiredTime) {
        Claims claims = Jwts.claims();
        claims.put("roles", roles);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiredTime)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .setClaims(claims)
                .compact();
    }

    public boolean validationToken(String token, String publicKey) {
        try {
            Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException exception) {
            log.error("Invalid JWT signature :{}", exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.error("Invalid JWT malformed :{}", exception.getMessage());
        } catch (ExpiredJwtException exception) {
            log.error("JWT token is expired :{}", exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.error("JWT token is unsupported :{} ", exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.error("JWT claims is not empty :{}", exception.getMessage());
        }
        return false;
    }

    public AuthResponse generateTokenFromRefreshToken(String refreshToken, String DEVICE_ID) throws Exception {
        /*
            1. Check refresh token used in dbs
            2. Check refresh token in dbs
            3. Create token and refreshToken
         */

        try {
            // 1. Abnormal : bat thuong in JWTAuthenticationFilter

            // 2.
            Token refreshTokenDB = tokenService.findTokenByValueAndNameAndType(refreshToken, "Refresh", "Bear")
                    .orElseThrow(() -> new Exception("Don't have anything refresh token"));
            Set<Role> roles = refreshTokenDB.getCreateBy().getRoles();
            List<String> roleList = new ArrayList<>();
            roles.forEach(role -> roleList.add(role.getRoleName().toString()));
            // 3.
            // create token
            String jwtToken = this.generateToken(refreshTokenDB.getCreateBy(), roleList, DEVICE_ID);

            //create refresh token
            String jwtRefreshToken = this.generateRefreshToken(refreshTokenDB.getCreateBy(), roleList, DEVICE_ID, refreshToken);
            return new AuthResponse(jwtToken, jwtRefreshToken);

        } catch (Exception exception) {
            throw new Exception(exception);
        }
    }

}
