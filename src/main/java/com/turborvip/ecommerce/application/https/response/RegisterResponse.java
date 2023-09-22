package com.turborvip.ecommerce.application.https.response;

import com.turborvip.ecommerce.domain.dto.Profile;
import com.turborvip.ecommerce.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private String token = null;
    private String refreshToken = null;
    private Profile profile;
}
