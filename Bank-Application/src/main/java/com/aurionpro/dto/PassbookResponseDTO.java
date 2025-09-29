package com.aurionpro.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassbookResponseDTO {

	private Long passbookId;
	private String accountNumber;
	private Double currentBalance;
	private TransactionResponseDTO transaction;
}
