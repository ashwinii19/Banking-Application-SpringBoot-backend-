package com.aurionpro.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.dto.UserRegisterDTO;
import com.aurionpro.dto.UserResponseDTO;
import com.aurionpro.dto.UserUpdateDTO;
import com.aurionpro.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}

	@PostMapping("/users")
	public ResponseEntity<UserResponseDTO> createUser(@Validated @RequestBody UserRegisterDTO userdto) {
	    UserResponseDTO userrespdto = userService.registerUser(userdto);

	    return ResponseEntity
	            .created(URI.create("/api/users/" + userrespdto.getId()))
	            .body(userrespdto);
	}

	
//	@PreAuthorize("hasAnyRole('ADMIN') or #id == principal.id")
	@GetMapping("/users/{id}")
	public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id, Authentication authentication) {
		UserResponseDTO userrespdto = userService.getUserById(id, authentication);
		return ResponseEntity.ok(userrespdto);
	}

//	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/users")
	public ResponseEntity<List<UserResponseDTO>> getAllUsers(Authentication authentication) {
		return ResponseEntity.ok(userService.getAllUsers(authentication));
	}

//	@PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
	@PutMapping("/users/{id}")
	public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
			@Validated @RequestBody UserUpdateDTO userdto, Authentication authentication) {
		UserResponseDTO updatedUser = userService.updateUser(id, userdto, authentication);
		return ResponseEntity.ok(updatedUser);
	}

//	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication authentication) {
		userService.deleteUser(id, authentication);
		return ResponseEntity.noContent().build();
	}

//	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/users/me")
	public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
		return ResponseEntity.ok(userService.getUserByUsername(authentication.getName()));
	}

}
