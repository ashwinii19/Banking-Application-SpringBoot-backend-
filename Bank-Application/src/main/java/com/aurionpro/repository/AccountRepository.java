package com.aurionpro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aurionpro.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long>{

	 List<Account> findByCustomerCustomerId(Long customerId);
	 
	 List<Account> findByIsAccountDeleted(String isAccountDeleted);
	 
	 Optional<Account> findByAccountNumber(String accountNumber);
	 
	 List<Account> findByCustomerCustomerIdAndIsAccountDeleted(Long customerId, String isAccountDeleted);
	 
	 boolean existsByAccountNumber(String accountNumber);

}
