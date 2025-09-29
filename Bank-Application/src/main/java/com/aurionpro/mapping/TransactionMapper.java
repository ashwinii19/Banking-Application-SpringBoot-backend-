package com.aurionpro.mapping;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.aurionpro.dto.TransactionRequestDTO;
import com.aurionpro.dto.TransactionResponseDTO;
import com.aurionpro.entity.Transaction;

@Component
public class TransactionMapper {
	
	private final ModelMapper modelMapper;
	
	public TransactionMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public Transaction toEntity(TransactionRequestDTO transreqdto) {
		Transaction transaction = modelMapper.map(transreqdto, Transaction.class);
		return transaction;
	}
		
	public TransactionResponseDTO toResponse(Transaction transaction) {
        TransactionResponseDTO dto = modelMapper.map(transaction, TransactionResponseDTO.class);
        if (transaction.getAccount() != null) {
            dto.setAccountNumber(transaction.getAccount().getAccountNumber());
            dto.setCurrentBalance(transaction.getAccount().getBalance());
        }
        if (transaction.getCustomer() != null) {
            dto.setCustomerId(transaction.getCustomer().getCustomerId());
        }
        return dto;
    }
    
	
	public List<TransactionResponseDTO> toResponseList(List<Transaction> transaction){
		return transaction.stream().map(this::toResponse).collect(Collectors.toList());
	}
}
