package com.aurionpro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {

	@NotBlank
	@Size(min=2)
	private String userName;
	
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$", message = "Password must be 8-20 characters, with upper, lower, digit, and special character")
	private String password;
	
	private Long roleName;
}
