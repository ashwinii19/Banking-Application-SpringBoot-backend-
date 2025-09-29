package com.aurionpro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequestDTO {

    @NotBlank
    @Pattern(regexp = "^(DEBIT|CREDIT|TRANSFER)$", message = "Invalid transaction type")
    private String transType;

    @DecimalMin(value = "1.00", message = "Amount must be greater than 1")
    private Double amount;
    
    
    @NotNull
    private String accountNumber;

    private Long customerId;
}
