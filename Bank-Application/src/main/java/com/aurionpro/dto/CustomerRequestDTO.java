package com.aurionpro.dto;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequestDTO {

	@Email
	@NotBlank
	@Column(unique=true,nullable = false)
	private String emailid;
	
	@NotBlank
	@Pattern(regexp = "^[0-9]{10}$")
	private String contactNo;
	
	@Past
	@NotNull
	private LocalDate dob;
	
	@Valid
	private AddressRequestDTO address;
	
	@Valid
	private AccountRequestDTO account;
}
