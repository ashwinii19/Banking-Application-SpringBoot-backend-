package com.aurionpro.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequestDTO {
	
	@NotBlank
	@Size(min=2)
	@Column(nullable=false)
	private String roleName;

}
