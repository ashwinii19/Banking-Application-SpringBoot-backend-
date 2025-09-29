package com.aurionpro.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.aurionpro.dto.LoginRequestDTO;
import com.aurionpro.dto.UserRegisterDTO;
import com.aurionpro.dto.UserResponseDTO;
import com.aurionpro.dto.UserUpdateDTO;

public interface UserService {

	public UserResponseDTO registerUser(UserRegisterDTO userdto);

	public UserResponseDTO getUserById(Long id, Authentication authentication);

	public List<UserResponseDTO> getAllUsers(Authentication authentication);

	public UserResponseDTO updateUser(Long id, UserUpdateDTO userdto, Authentication authentication);

	public void deleteUser(Long id, Authentication authentication);
	
	String login(LoginRequestDTO loginRequestDTO);
	
	UserResponseDTO getUserByUsername(String userName);
}
