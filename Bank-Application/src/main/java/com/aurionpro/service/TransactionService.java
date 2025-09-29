package com.aurionpro.service;

import java.util.List;

import com.aurionpro.dto.TransactionRequestDTO;
import com.aurionpro.dto.TransactionResponseDTO;

public interface TransactionService {

	TransactionResponseDTO createTransaction(TransactionRequestDTO dto);

    List<TransactionResponseDTO> getTransactionsByAccountId(Long accountId);

    List<TransactionResponseDTO> getTransactionsByCustomerId(Long customerId);

    TransactionResponseDTO getTransactionById(Long id);

}

