package com.turborvip.ecommerce.application.services.impl;

import com.turborvip.ecommerce.application.https.response.AuthResponse;
import com.turborvip.ecommerce.application.services.TokenService;
import com.turborvip.ecommerce.application.services.UserDeviceService;
import com.turborvip.ecommerce.domain.entity.Role;
import com.turborvip.ecommerce.domain.entity.Token;
import com.turborvip.ecommerce.domain.entity.User;
import com.turborvip.ecommerce.domain.entity.UserDevice;
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

    private final Long dueTimeAccessToken = 3000L;
    private final Long dueTimeRefreshToken = 86400L;

    @Autowired
    private final TokenService tokenService;

    @Autowired
    private final UserDeviceService userDeviceService;

    public String generateToken(User user, List<String> roles, String DEVICE_ID) {
        try {
            // TODO generate secret key
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(4096);
            KeyPair pair = keyPairGen.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());

            // TODO generate jwt
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp expiredTime = new Timestamp(System.currentTimeMillis() + dueTimeAccessToken * 1000);
            String jwtGenerate = this.generateTokenUtil(user.getUsername(), roles, privateKey, expiredTime);

            // TODO check device
            UserDevice userDevice = userDeviceService.findDeviceByUserIdAndDeviceId(user.getId(), DEVICE_ID).orElse(null);
            if (userDevice != null) {
                userDeviceService.updateLastLogin(now, userDevice.getId());
            } else {
                UserDevice userDeviceNew = new UserDevice(DEVICE_ID, now, null, null, null);
                userDeviceNew.setCreateBy(user);
                userDeviceNew.setUpdateBy(user);
                userDevice = userDeviceService.create(userDeviceNew);
            }

            // TODO update or create token
            Token tokenExisted = tokenService.findFirstTokenByUserIdAndTypeAndDeviceId(user.getId(), "Access", DEVICE_ID).orElse(null);
            if (tokenExisted != null) {
                // update value, publicKey, expiredTime, updateAt
                tokenService.updateTokenWithValueExpiredTime(tokenExisted, now, jwtGenerate, expiredTime, publicKeyString, null);
            } else {
                Token token = new Token(null, null, "Access", jwtGenerate, publicKeyString, new ArrayList<>(), expiredTime, userDevice);
                token.setCreateBy(user);
                token.setUpdateBy(user);
                tokenService.create(token);
            }
            return jwtGenerate;
        }catch (Exception exception){
            log.error("generate access Token fail! " + exception.getMessage());
            return null;
        }

    }

    public String generateRefreshToken(User user, List<String> roles, String DEVICE_ID, String refreshToken) throws NoSuchAlgorithmException {
        try{
            // TODO generate secret key
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(4096);
            KeyPair pair = keyPairGen.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            // TODO generate jwt
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp expiredTime = new Timestamp(System.currentTimeMillis() + dueTimeRefreshToken * 1000);

            String jwtGenerate = this.generateTokenUtil(user.getUsername(), roles, privateKey, expiredTime);

            // TODO check device
            UserDevice userDevice = userDeviceService.findDeviceByUserIdAndDeviceId(user.getId(), DEVICE_ID).orElse(null);
            if (userDevice != null) {
                userDeviceService.updateLastLogin(now, userDevice.getId());
            } else {
                UserDevice userDeviceNew = new UserDevice(DEVICE_ID, now, null, null, null);
                userDeviceNew.setCreateBy(user);
                userDeviceNew.setUpdateBy(user);
                userDevice = userDeviceService.create(userDeviceNew);
            }

            // TODO check token
            Token tokenExisted = tokenService.findFirstTokenByUserIdAndTypeAndDeviceId(user.getId(), "Refresh", DEVICE_ID).orElse(null);
            if (tokenExisted != null) {
                // update value, publicKey, expiredTime, updateAt
                tokenService.updateTokenWithValueExpiredTime(tokenExisted, now, jwtGenerate, expiredTime, publicKeyString, refreshToken);
            } else {
                Token token = new Token(null, null, "Refresh", jwtGenerate, publicKeyString, new ArrayList<>(), expiredTime, userDevice);
                token.setCreateBy(user);
                token.setUpdateBy(user);
                tokenService.create(token);
            }
            return jwtGenerate;
        }catch (Exception exception){
            log.error("generate refreshToken fail!");
            return null;
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
            Token refreshTokenDB = tokenService.findTokenByValueAndType(refreshToken, "Refresh")
                    .orElseThrow(() -> new Exception("Don't have anything refresh token"));
            Set<Role> roles = refreshTokenDB.getCreateBy().getRoles();
            List<String> roleList = new ArrayList<>();
            roles.forEach(role -> roleList.add(role.getRoleName().toString()));
            // 3.
            // create token
            String jwtToken = this.generateToken(refreshTokenDB.getCreateBy(), roleList, DEVICE_ID);

            //create refresh token
            String jwtRefreshToken = this.generateRefreshToken(refreshTokenDB.getCreateBy(), roleList, DEVICE_ID, refreshToken);
            return new AuthResponse(jwtToken,jwtRefreshToken);

        } catch (Exception exception) {
            throw new Exception(exception);
        }
    }

}
