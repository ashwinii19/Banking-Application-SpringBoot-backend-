package com.aurionpro.service;

import java.util.List;

import com.aurionpro.dto.AccountRequestDTO;
import com.aurionpro.dto.AccountResponseDTO;
import com.aurionpro.dto.AccountUpdateDTO;

public interface AccountService {

	public AccountResponseDTO createAccount(AccountRequestDTO accreqdto);
	
	public AccountResponseDTO getAccountById(Long id);
	
	public List<AccountResponseDTO> getAllAccounts();
	
	public AccountResponseDTO updateAccount(Long id, AccountUpdateDTO accupdto);
	
	 List<AccountResponseDTO> getAccountsByCustomerId(Long customerId);
	
	public void deleteAccount(Long id);
}
