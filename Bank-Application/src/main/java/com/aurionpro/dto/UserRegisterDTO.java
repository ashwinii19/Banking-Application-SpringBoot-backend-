package com.aurionpro.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterDTO {

	private String userName;
	private String password;
	private List<String> roles;
	private CustomerRequestDTO customer;
}
