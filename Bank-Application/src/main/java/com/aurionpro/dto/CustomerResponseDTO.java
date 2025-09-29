package com.aurionpro.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerResponseDTO {

	private Long customerId;
	private String emailid;
	private String contactNo;
	private LocalDate dob;
	
	private AddressResponseDTO address;
	
	private List<AccountResponseDTO> accounts;
}
