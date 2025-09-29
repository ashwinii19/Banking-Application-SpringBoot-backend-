package com.aurionpro.mapping;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.aurionpro.dto.PassbookResponseDTO;
import com.aurionpro.dto.TransactionResponseDTO;
import com.aurionpro.entity.Passbook;
import com.aurionpro.entity.Transaction;

@Component
public class PassbookMapper {

	private final ModelMapper modelMapper;

	public PassbookMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public PassbookResponseDTO toResponse(Passbook passbook, Transaction transaction) {
		PassbookResponseDTO passbookResponse = modelMapper.map(passbook, PassbookResponseDTO.class);

		TransactionResponseDTO txnDTO = modelMapper.map(transaction, TransactionResponseDTO.class);
		txnDTO.setAccountNumber(passbook.getAccountNumber());
		txnDTO.setCurrentBalance(passbook.getBalance());

		passbookResponse.setTransaction(txnDTO);
		passbookResponse.setCurrentBalance(passbook.getBalance());

		return passbookResponse;
	}
}
