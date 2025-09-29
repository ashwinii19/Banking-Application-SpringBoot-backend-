package com.aurionpro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

	@NotBlank
	private String userName;
	
	@NotBlank
    private String password;
}
