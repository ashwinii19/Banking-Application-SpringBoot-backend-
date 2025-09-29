package com.aurionpro.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {

	private Long userId;
	private String userName;
    private List<RoleResponseDTO> roles;
    private String token;
}
