package com.turborvip.ecommerce.application.services.impl;

import com.turborvip.ecommerce.application.configuration.exception.VsException;
import com.turborvip.ecommerce.application.constants.CommonConstant;
import com.turborvip.ecommerce.application.constants.DevMessageConstant;
import com.turborvip.ecommerce.application.constants.EnumRole;
import com.turborvip.ecommerce.application.repositories.RoleRepository;
import com.turborvip.ecommerce.application.repositories.UserRepository;
import com.turborvip.ecommerce.application.services.UserService;
import com.turborvip.ecommerce.application.util.RegexValidator;
import com.turborvip.ecommerce.domain.dto.UserDTO;
import com.turborvip.ecommerce.domain.entity.Role;
import com.turborvip.ecommerce.domain.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public User create(User user, HttpServletRequest request) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User registerUser(UserDTO userDTO) {
        try {
            // Todo check username
            if (userRepository.existsByUsername(userDTO.getUserName())) {
                throw new VsException(String.format(DevMessageConstant.Common.EXITS_USERNAME, userDTO.getUserName()));
            }

            // Todo check birthday
            SimpleDateFormat sdf = new SimpleDateFormat(CommonConstant.FORMAT_DATE_PATTERN);
            Date birthday = null;
            if (userDTO.getBirthday() != null) {
                try {
                    birthday = sdf.parse(userDTO.getBirthday());
                } catch (Exception e) {
                    log.error(e.getMessage());
                    throw new VsException(String.format(e.getMessage()));
                }
            }

            // Todo check phone number
            if (userDTO.getPhone() != null) {
                RegexValidator checkPhone = new RegexValidator(CommonConstant.REGEX_PHONE_NUMBER);
                if (!checkPhone.validate(userDTO.getPhone())) {
                    throw new VsException(DevMessageConstant.Common.PHONE_WRONG_FORMAT);
                }
            }

            // Todo check email number
            if (userDTO.getEmail() != null) {
                RegexValidator checkEmail = new RegexValidator(CommonConstant.REGEX_EMAIL);
                if (!checkEmail.validate(userDTO.getEmail())) {
                    throw new VsException(DevMessageConstant.Common.EMAIL_WRONG_FORMAT);
                }
            }

            // Todo check password empty
            if (userDTO.getPassword() == null) {
                throw new VsException(DevMessageConstant.Common.PASSWORD_NOT_EMPTY);
            }

            // Todo check password format
            RegexValidator checkPassword = new RegexValidator(CommonConstant.REGEX_PASSWORD);
            if (!checkPassword.validate(userDTO.getPassword())) {
                throw new VsException(DevMessageConstant.Common.PASSWORD_WRONG_FORMAT);
            }

            // Todo create new user
            Role roleUser = roleRepository.findRoleByRoleName(EnumRole.ROLE_USER);
            User user = new User(userDTO.getFullName(), userDTO.getUserName(), new BCryptPasswordEncoder().encode(userDTO.getPassword()), userDTO.getEmail(), birthday, userDTO.getGender(), userDTO.getPhone(), userDTO.getAddress(), userDTO.getAvatar(), new HashSet<>());
            user = userRepository.save(user);
            user.getRoles().add(roleUser);

            return user;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void addToUser(String username, String role_name) {

        User user = userRepository.findByUsername(username).get();
        Role role = roleRepository.findRoleByRoleName(EnumRole.valueOf(role_name));

        user.getRoles().add(role);
    }
}
