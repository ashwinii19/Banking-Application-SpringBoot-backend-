package com.aurionpro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aurionpro.entity.Role;

public interface RoleRespository extends JpaRepository<Role, Long>{
	
	Optional<Role> findByRoleName(String name);
}
