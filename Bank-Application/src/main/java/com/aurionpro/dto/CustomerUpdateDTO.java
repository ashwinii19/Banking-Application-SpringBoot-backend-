package com.aurionpro.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerUpdateDTO {

	@Email
	@NotBlank
	@Column(unique = true, nullable = false)
	private String emailid;

	@NotBlank
	@Pattern(regexp = "^[0-9]{10}$")
	private String contactNo;

	@Column(nullable = false)
	private String city;

	@Column(nullable = false)
	private String state;

	@Column(nullable = false, length = 6)
	private String pincode;

	@NotBlank
	@Pattern(regexp = "^(SAVINGS|CURRENT|SALARY|FD)$", message = "Invalid account type")
	private String accountType;

	@NotNull
	@DecimalMin(value = "500.00", message = "Minimum balance must be ₹1000.00")
	private Double balance;
}
