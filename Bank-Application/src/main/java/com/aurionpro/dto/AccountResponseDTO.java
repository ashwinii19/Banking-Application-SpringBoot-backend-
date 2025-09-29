package com.aurionpro.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountResponseDTO {

	private Long accountId;
	private String accountNumber;
	private String accountType;
	private Double balance;
	private Long customerId;
}
