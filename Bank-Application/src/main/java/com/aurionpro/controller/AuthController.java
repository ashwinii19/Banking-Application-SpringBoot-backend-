package com.aurionpro.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.dto.LoginRequestDTO;
import com.aurionpro.dto.LoginResponseDTO;
import com.aurionpro.dto.RoleResponseDTO;
import com.aurionpro.dto.UserRegisterDTO;
import com.aurionpro.dto.UserResponseDTO;
import com.aurionpro.entity.Role;
import com.aurionpro.entity.User;
import com.aurionpro.exception.ResourceNotFoundException;
import com.aurionpro.repository.UserRepository;
import com.aurionpro.security.JwtTokenProvider;
import com.aurionpro.service.UserService;

import jakarta.validation.Valid;
import lombok.Data;

@RestController
@RequestMapping("/api/auth")
@Data
public class AuthController {

    private final UserService userService;
    private final UserRepository userResRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider, UserRepository userResRepository) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userResRepository = userResRepository;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        try {
            UserResponseDTO savedUser = userService.registerUser(userRegisterDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "message", "User registered successfully",
                            "user", savedUser
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Something went wrong"));
        }
    }


    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            String token = userService.login(loginRequest);

            User user = userResRepository.findByUserName(loginRequest.getUserName())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

            LoginResponseDTO loginResponse = new LoginResponseDTO();
            loginResponse.setUserId(user.getUserId());
            loginResponse.setUserName(user.getUserName());
            loginResponse.setRoles(mapRoles(user.getRoles()));
            loginResponse.setToken(token);

            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Username or password is incorrect"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong"));
        }
    }
    

    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        try {
            List<UserResponseDTO> users = userService.getAllUsers(authentication);
            return ResponseEntity.ok(users);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    private List<RoleResponseDTO> mapRoles(List<Role> roles) {
        return roles.stream()
                .map(role -> new RoleResponseDTO(role.getRoleId(), role.getRoleName()))
                .collect(Collectors.toList());
    }
}
