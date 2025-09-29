package com.aurionpro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aurionpro.dto.RoleRequestDTO;
import com.aurionpro.dto.RoleResponseDTO;
import com.aurionpro.entity.Role;
import com.aurionpro.entity.User;
import com.aurionpro.exception.ResourceNotFoundException;
import com.aurionpro.mapping.RoleMapper;
import com.aurionpro.repository.RoleRespository;
import com.aurionpro.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService{

	private final RoleMapper roleMapper;
	private final RoleRespository roleRespository;
	private final UserRepository userRepository;

	@Autowired
	public RoleServiceImpl(RoleMapper roleMapper, RoleRespository roleRespository, UserRepository userRepository) {
		this.roleMapper = roleMapper;
		this.roleRespository = roleRespository;
		this.userRepository = userRepository;
	}

	@Override
	public RoleResponseDTO createRole(RoleRequestDTO roleRequestDTO) {
		Role role = roleMapper.toEntity(roleRequestDTO);
		Role saved = roleRespository.save(role);
		return roleMapper.toResponse(saved);
	}

	@Override
    public List<RoleResponseDTO> getRolesByUserId(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        
        List<Role> roles = user.getRoles();
        if (roles == null || roles.isEmpty()) {
            throw new ResourceNotFoundException("No roles assigned to this user.");
        }
        
        return roleMapper.toResponseList(roles);
    }

	@Override
	public void delete(Long id) {
		Role role =  roleRespository.findById(id).
				orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));
		roleRespository.delete(role);
	}

	@Override
	public List<RoleResponseDTO> getAllRole() {
		List<Role> role = roleRespository.findAll();
		return roleMapper.toResponseList(role);
	}
	
	
}
