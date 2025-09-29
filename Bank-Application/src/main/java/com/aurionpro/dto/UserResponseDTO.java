package com.aurionpro.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

	private Long id;
	private String userName;
	private String role;
	private String emailid;
	
	private CustomerResponseDTO customer;
	
	private AddressResponseDTO address;

}
