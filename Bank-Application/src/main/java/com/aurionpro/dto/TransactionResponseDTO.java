package com.aurionpro.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionResponseDTO {

	private Long transId;
    private String transType;
    private Double amount;
    private LocalDate date;
    private String accountNumber;
    private Double currentBalance;
    private Long customerId;
}