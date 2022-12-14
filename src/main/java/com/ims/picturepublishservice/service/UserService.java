package com.ims.picturepublishservice.service;


import com.ims.picturepublishservice.dto.SignUpRequest;
import com.ims.picturepublishservice.entity.Role;
import com.ims.picturepublishservice.entity.User;
import com.ims.picturepublishservice.exception.AlreadyExistException;
import com.ims.picturepublishservice.exception.EntityNotFoundException;
import com.ims.picturepublishservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleService roleService;

    public User registerUser(SignUpRequest signUpRequest) throws AlreadyExistException,
            EntityNotFoundException {

        if (isUserNameExists(signUpRequest.getUserName())) {
            throw new AlreadyExistException(String.format("Username %s already exists",
                    signUpRequest.getUserName()));
        }

        if (isEmailExists(signUpRequest.getEmail())) {
            throw new AlreadyExistException(String.format("Email %s already exists",
                    signUpRequest.getEmail()));
        }

        Role userRole = roleService.getUserRole();

        User user = User.builder().email(signUpRequest.getEmail())
                .username(signUpRequest.getUserName())
                .password(encoder.encode(signUpRequest.getPassword()))
                .roles(new HashSet<Role>() {{
                    add(userRole);
                }})
                .build();

        return this.userRepository.save(user);
    }


    public User findByUserName(String userName) throws EntityNotFoundException {
        return this.userRepository.findByUsername(userName)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found with username: " + userName));
    }

    public boolean isUserNameExists(String userName) {
        return this.userRepository.existsByUsername(userName);
    }

    public void save(User user) {
        this.userRepository.save(user);
    }

    private boolean isEmailExists(String email) {
        return this.userRepository.existsByEmail(email);
    }
}
