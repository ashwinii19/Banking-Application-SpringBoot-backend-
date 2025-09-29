package com.aurionpro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aurionpro.dto.LoginRequestDTO;
import com.aurionpro.dto.UserRegisterDTO;
import com.aurionpro.dto.UserResponseDTO;
import com.aurionpro.dto.UserUpdateDTO;
import com.aurionpro.entity.Role;
import com.aurionpro.entity.User;
import com.aurionpro.exception.ResourceNotFoundException;
import com.aurionpro.exception.UserApiException;
import com.aurionpro.mapping.UserMapper;
import com.aurionpro.repository.RoleRespository;
import com.aurionpro.repository.UserRepository;
import com.aurionpro.security.JwtTokenProvider;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userrepository;
    private final RoleRespository roleRespository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, UserRepository userrepository,
                           RoleRespository roleRespository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userMapper = userMapper;
        this.userrepository = userrepository;
        this.roleRespository = roleRespository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserResponseDTO registerUser(UserRegisterDTO userdto) {
        userdto.setPassword(passwordEncoder.encode(userdto.getPassword()));

        List<String> roleNames = userdto.getRoles().stream()
                                        .map(String::toUpperCase)
                                        .toList();

        List<Role> roles = roleNames.stream()
                .map(name -> roleRespository.findByRoleName(name)
                        .orElseThrow(() -> new IllegalArgumentException("Role " + name + " not found")))
                .toList();

        if (roleNames.contains("ROLE_CUSTOMER") && userdto.getCustomer() == null) {
            throw new IllegalArgumentException("Customer details are required for CUSTOMER role.");
        }

        if (roleNames.contains("ROLE_ADMIN") && userdto.getCustomer() != null) {
            throw new IllegalArgumentException("Customer details should not be provided for ADMIN role.");
        }

        User user = userMapper.toEntity(userdto, roles);
        User saved = userrepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponseDTO getUserByUsername(String userName) {
        User user = userrepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + userName));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponseDTO getUserById(Long id, Authentication authentication) {
        User loggedInUser = userrepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = loggedInUser.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getRoleName()));

        if (!isAdmin && !loggedInUser.getUserId().equals(id)) {
            throw new AccessDeniedException("You do not have permission to view this user");
        }

        User user = userrepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not Found!"));

        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers(Authentication authentication) {
        User loggedInUser = userrepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = loggedInUser.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getRoleName()));

        if (!isAdmin) {
            throw new AccessDeniedException("Only admins can view all users");
        }

        List<User> activeUsers = userrepository.findByIsUserDeleted("N");
        return userMapper.toResponseList(activeUsers);
    }

    @Override
    public void deleteUser(Long id, Authentication authentication) {
        User loggedInUser = userrepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = loggedInUser.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getRoleName()));

        if (!isAdmin) {
            throw new AccessDeniedException("Only admins can delete users");
        }

        User user = userrepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not Found!"));
        user.setIsUserDeleted("Y");
        userrepository.save(user);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateDTO userdto, Authentication authentication) {
        User loggedInUser = userrepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Caller user not found"));

        boolean isAdmin = loggedInUser.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getRoleName()));

        if (!isAdmin && !loggedInUser.getUserId().equals(id)) {
            throw new AccessDeniedException("You do not have permission to update this user");
        }

        User user = userrepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not Found!"));

        if (!isAdmin && userdto.getRoleName() != null) {
            throw new AccessDeniedException("Customers cannot change their roles");
        }

        userMapper.applyUpdate(userdto, user, null);
        User updated = userrepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Override
    public String login(LoginRequestDTO loginRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUserName(), loginRequestDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            return token;
        } catch (BadCredentialsException e) {
            throw new UserApiException("Username or Password is incorrect");
        }
    }
}

