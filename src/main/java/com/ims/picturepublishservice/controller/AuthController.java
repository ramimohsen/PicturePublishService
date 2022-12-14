package com.ims.picturepublishservice.controller;


import com.ims.picturepublishservice.dto.JwtResponse;
import com.ims.picturepublishservice.dto.LoginRequest;
import com.ims.picturepublishservice.dto.SignUpRequest;
import com.ims.picturepublishservice.entity.User;
import com.ims.picturepublishservice.exception.AlreadyExistException;
import com.ims.picturepublishservice.exception.EntityNotFoundException;
import com.ims.picturepublishservice.security.jwt.JwtUtils;
import com.ims.picturepublishservice.security.services.UserDetailsImpl;
import com.ims.picturepublishservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JwtUtils jwtUtils;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }


    @PostMapping("/signup")
    public User registerUser(@Valid @RequestBody SignUpRequest signUpRequest) throws EntityNotFoundException,
            AlreadyExistException {
        return this.userService.registerUser(signUpRequest);
    }
}
