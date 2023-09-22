package com.turborvip.ecommerce.domain.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserDTO {
    private String fullName;
    private String userName;
    private String password;
    private String email;
    private String birthday;
    private String gender;
    private String phone;
    private String address;
    private String avatar;
}
