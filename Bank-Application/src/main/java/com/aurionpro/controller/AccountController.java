package com.aurionpro.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.dto.AccountRequestDTO;
import com.aurionpro.dto.AccountResponseDTO;
import com.aurionpro.dto.AccountUpdateDTO;
import com.aurionpro.service.AccountService;

@RestController
@RequestMapping("/api")
public class AccountController {

	private AccountService accountService;

	@Autowired
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@PostMapping("/accounts")
	public ResponseEntity<AccountResponseDTO> createAccount(@Validated @RequestBody AccountRequestDTO accreqdto) {
		AccountResponseDTO accdto = accountService.createAccount(accreqdto);
		return ResponseEntity.created(URI.create("/api/accounts/" + accdto.getAccountId())).body(accdto);

	}

	@GetMapping("/accounts/{id}")
	public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable Long id) {
		return ResponseEntity.ok(accountService.getAccountById(id));
	}

	@GetMapping("/accounts")
	public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
		return ResponseEntity.ok(accountService.getAllAccounts());
	}

	@PutMapping("/accounts/{id}")
	public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable Long id,
			@Validated @RequestBody AccountUpdateDTO accupdto) {
		return ResponseEntity.ok(accountService.updateAccount(id, accupdto));
	}

	@GetMapping("/accounts/customer/{customerId}")
	public ResponseEntity<List<AccountResponseDTO>> getAccountsByCustomerId(@PathVariable Long customerId) {
		return ResponseEntity.ok(accountService.getAccountsByCustomerId(customerId));
	}

	@DeleteMapping("/accounts/{id}")
	public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
		accountService.deleteAccount(id);
		return ResponseEntity.noContent().build();
	}

}
