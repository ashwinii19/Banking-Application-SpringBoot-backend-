package com.aurionpro.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.dto.RoleRequestDTO;
import com.aurionpro.dto.RoleResponseDTO;
import com.aurionpro.service.RoleService;

@RestController
@RequestMapping("/api")
public class RoleController {

	private RoleService roleService;

	@Autowired
	public RoleController(RoleService roleService) {
		super();
		this.roleService = roleService;
	}

	@PostMapping("/roles")
	public ResponseEntity<RoleResponseDTO> createRole(@Validated @RequestBody RoleRequestDTO roledto) {
		RoleResponseDTO rolerespdto = roleService.createRole(roledto);
		return ResponseEntity.created(URI.create("/api/roles" + rolerespdto.getRoleId())).build();
	}

	@GetMapping("/roles/{userId}")
	public ResponseEntity<List<RoleResponseDTO>> getRolesByUserId(@PathVariable Long userId) {
	    List<RoleResponseDTO> roles = roleService.getRolesByUserId(userId);
	    return ResponseEntity.ok(roles);
	}


	@GetMapping("/roles")
	public ResponseEntity<List<RoleResponseDTO>> getAllRole() {
		return ResponseEntity.ok(roleService.getAllRole());
	}

	@DeleteMapping("/roles/{id}")
	public ResponseEntity<RoleResponseDTO> delete(@PathVariable Long id) {
		roleService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
