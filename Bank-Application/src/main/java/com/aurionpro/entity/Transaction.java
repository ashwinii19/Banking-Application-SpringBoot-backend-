package com.aurionpro.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transId;

    @Column(nullable = false)
    @Pattern(regexp = "^(DEBIT|CREDIT|TRANSFER)$", message = "Invalid transaction type")
    private String transType; 

    @Column(nullable = false)
    @DecimalMin(value = "1.00", message = "Amount must be greater than 1")
    private Double amount;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "account_number", referencedColumnName = "accountNumber", nullable = true)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    public String getAccountNumber() {
        return account != null ? account.getAccountNumber() : null;
    }
}
