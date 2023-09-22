package com.turborvip.ecommerce.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.turborvip.ecommerce.application.constants.CommonConstant;
import com.turborvip.ecommerce.domain.entity.base.AbstractBase;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "users",schema = "account")
public class User extends AbstractBase implements UserDetails {

    @Column(name = "fullname")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fullName;

    @NotEmpty(message = "Username must not be empty")
    @Size(min = 6, max = 20, message = "Username must be between 6 and 20 characters")
    private String username;


    @NotEmpty(message = "Password must not be empty")
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+-=\\[\\]{};:'\"<>,.?/]).{8,}$",
//            message = "Password is not valid")
    @Column(name = "password", nullable = false)
    private String password;

    //    @Pattern(regexp = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
//            message = "Email is not format")
//    @Column(name = "email", nullable = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;

    @JsonFormat(pattern = CommonConstant.FORMAT_DATE_PATTERN)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "gender")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String gender;
    //    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 characters")
    @Column(name = "phone")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phone;

    @Column(name = "address")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String address;

    @Column(name = "avatar")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String avatar;


    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "user_role",schema = "account",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(fullName, username, password, email, birthday, gender, phone, address, avatar);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roles.stream().forEach(i -> authorities.add(new SimpleGrantedAuthority(i.getRoleName().name())));
        return List.of(new SimpleGrantedAuthority(authorities.toString()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
