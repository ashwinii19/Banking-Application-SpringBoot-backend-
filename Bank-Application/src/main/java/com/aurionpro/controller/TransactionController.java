package com.aurionpro.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.dto.TransactionRequestDTO;
import com.aurionpro.dto.TransactionResponseDTO;
import com.aurionpro.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public TransactionResponseDTO createTransaction(@Valid @RequestBody TransactionRequestDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String customerEmail = authentication.getName(); 
        return transactionService.createTransaction(dto);
    }

    @GetMapping("/customers/{customerId}")
//    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public List<TransactionResponseDTO> getByCustomerId(@PathVariable Long customerId) {
        return transactionService.getTransactionsByCustomerId(customerId);
    }

    @GetMapping("/accounts/{accountId}")
//    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public List<TransactionResponseDTO> getByAccountId(@PathVariable Long accountId) {
        return transactionService.getTransactionsByAccountId(accountId);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public TransactionResponseDTO getById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }
}

