package com.aurionpro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequestDTO {

    @NotBlank
    @Pattern(regexp = "^(SAVINGS|CURRENT|SALARY|FD)$", message = "Invalid account type")
    private String accountType;

    @NotNull
    @DecimalMin(value = "500.00", message = "Minimum balance must be ₹500.00")
    private Double balance;
    
    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be a positive number")
    private Long customerId;
}
