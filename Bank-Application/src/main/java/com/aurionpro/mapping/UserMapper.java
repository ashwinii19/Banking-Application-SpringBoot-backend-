package com.aurionpro.mapping;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.aurionpro.dto.CustomerResponseDTO;
import com.aurionpro.dto.UserRegisterDTO;
import com.aurionpro.dto.UserResponseDTO;
import com.aurionpro.dto.UserUpdateDTO;
import com.aurionpro.entity.Customer;
import com.aurionpro.entity.Role;
import com.aurionpro.entity.User;

@Component
public class UserMapper {

	private final ModelMapper modelMapper;

	public UserMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	
	public User toEntity(UserRegisterDTO dto, List<Role> roles) {
	    User user = modelMapper.map(dto, User.class);

	    if (dto.getCustomer() != null) {
	        Customer customer = modelMapper.map(dto.getCustomer(), Customer.class);
	        user.setCustomer(customer);
	    }

	    user.setIsUserDeleted("N");
	    user.setRoles(roles);

	    return user;
	}


	public UserResponseDTO toResponse(User user) {
		UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);
		if (user.getCustomer() != null) {
			 CustomerResponseDTO customerDTO = modelMapper.map(user.getCustomer(), CustomerResponseDTO.class);
			    userResponseDTO.setCustomer(customerDTO);
		}
		if (user.getRoles() != null && !user.getRoles().isEmpty()) {
			String roleNames = user.getRoles().stream().map(Role::getRoleName).collect(Collectors.joining(", "));
			userResponseDTO.setRole(roleNames);
		}
		return userResponseDTO;
	}

	public List<UserResponseDTO> toResponseList(List<User> users) {
		return users.stream().map(this::toResponse).collect(Collectors.toList());
	}

	public static void applyUpdate(UserUpdateDTO dto, User user, List<Role> roles) {
		if (dto.getUserName() != null && !dto.getUserName().isBlank()) {
			user.setUserName(dto.getUserName());
		}
		if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
			user.setPassword(dto.getPassword());
		}
		if (roles != null && !roles.isEmpty()) {
			user.getRoles().clear();
			user.getRoles().addAll(roles);
		}
	}
}
