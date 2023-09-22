package com.turborvip.ecommerce.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Profile {
    private String fullName;
    private String email;
    private String birthday;
    private String gender;
    private String phone;
    private String address;
    private String avatar;
}
