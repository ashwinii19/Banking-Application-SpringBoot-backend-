package com.aurionpro.mapping;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.aurionpro.dto.AccountRequestDTO;
import com.aurionpro.dto.AccountResponseDTO;
import com.aurionpro.dto.AccountUpdateDTO;
import com.aurionpro.entity.Account;

@Component
public class AccountMapper {

	private final ModelMapper modelMapper;

	public AccountMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}
	
	public Account toEntity(AccountRequestDTO accreqdto) {
		Account account = modelMapper.map(accreqdto, Account.class);
		account.setAccountId(null);
		account.setIsAccountDeleted("N");
		return account;
	}
	
	public AccountResponseDTO toResponse(Account account) {
		AccountResponseDTO accrespdto = modelMapper.map(account, AccountResponseDTO.class);
        if (account.getCustomer() != null) {
        	accrespdto.setCustomerId(account.getCustomer().getCustomerId());
        }
        return accrespdto;
	}
	
	public List<AccountResponseDTO> toResponseList(List<Account> account){
		return account.stream().map(this::toResponse).collect(Collectors.toList());
	}
	
	public static void applyUpdate(AccountUpdateDTO accupdto, Account account) {
	    if(account == null) {
	        throw new IllegalArgumentException("Account cannot be null");
	    }
	    if (accupdto.getAccountType() != null) {
	        account.setAccountType(accupdto.getAccountType());
	    }
	    if (accupdto.getBalance() != null) {
	        if(accupdto.getBalance() < 500.00) {
	            throw new IllegalArgumentException("Balance must be greater than or equal to 500");
	        }
	        account.setBalance(accupdto.getBalance());
	    }
	}



	
}
