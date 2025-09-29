package com.aurionpro.service;

import java.util.List;

import com.aurionpro.dto.RoleRequestDTO;
import com.aurionpro.dto.RoleResponseDTO;

public interface RoleService {

	public RoleResponseDTO createRole(RoleRequestDTO roleRequestDTO);
	public List<RoleResponseDTO> getRolesByUserId(Long userId);
	public List<RoleResponseDTO> getAllRole();
	public void delete(Long id);
}
