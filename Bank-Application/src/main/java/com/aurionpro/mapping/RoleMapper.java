package com.aurionpro.mapping;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.aurionpro.dto.RoleRequestDTO;
import com.aurionpro.dto.RoleResponseDTO;
import com.aurionpro.entity.Role;

@Component
public class RoleMapper {

	private final ModelMapper modelMapper;

	public RoleMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public Role toEntity(RoleRequestDTO rolereqdto) {
		Role role = modelMapper.map(rolereqdto, Role.class);
		return role;
	}

	public RoleResponseDTO toResponse(Role role) {
		RoleResponseDTO rolerespdto = modelMapper.map(role, RoleResponseDTO.class);
		return rolerespdto;
	}

	public List<RoleResponseDTO> toResponseList(List<Role> role) {
		return role.stream().map(this::toResponse).toList();
	}

}
