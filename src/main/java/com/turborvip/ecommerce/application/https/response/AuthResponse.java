package com.turborvip.ecommerce.application.https.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private  String token = null;
    private  String refreshToken = null;
}
