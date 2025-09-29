package com.aurionpro.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.aurionpro.dto.TransactionRequestDTO;
import com.aurionpro.dto.TransactionResponseDTO;
import com.aurionpro.entity.Account;
import com.aurionpro.entity.Customer;
import com.aurionpro.entity.Transaction;
import com.aurionpro.entity.User;
import com.aurionpro.exception.ResourceNotFoundException;
import com.aurionpro.mapping.TransactionMapper;
import com.aurionpro.repository.AccountRepository;
import com.aurionpro.repository.CustomerRepository;
import com.aurionpro.repository.TransactionRepository;
import com.aurionpro.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;
	private final CustomerRepository customerRepository;
	private final AccountRepository accountRepository;
	private final TransactionMapper transactionMapper;
	private final UserRepository userRepo;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionMapper transactionMapper,
			AccountRepository accountRepository, CustomerRepository customerRepository, UserRepository userRepo) {
		this.transactionRepository = transactionRepository;
		this.transactionMapper = transactionMapper;
		this.accountRepository = accountRepository;
		this.customerRepository = customerRepository;
		this.userRepo = userRepo;
	}

	private boolean hasRole(User user, String roleName) {
		return user.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("ROLE_" + roleName));
	}

	@Override
	public TransactionResponseDTO createTransaction(TransactionRequestDTO dto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String loggedInUserName = authentication.getName();

		User loggedInUser = userRepo.findByUserName(loggedInUserName)
				.orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"));

		if (!hasRole(loggedInUser, "CUSTOMER")) {
			throw new IllegalArgumentException("Only customers can perform transactions");
		}

		Customer customer = loggedInUser.getCustomer();
		if (customer == null) {
			throw new IllegalStateException("Logged-in user has no associated customer profile");
		}

		double amount = dto.getAmount();
		String transType = dto.getTransType().toUpperCase();

		List<Account> userAccounts = customer.getAccounts();
		if (userAccounts == null || userAccounts.isEmpty()) {
			throw new IllegalStateException("Customer has no accounts");
		}
		Account sourceAccount = userAccounts.get(0);

		switch (transType) {
		case "DEBIT":
			if (!sourceAccount.getAccountNumber().equals(dto.getAccountNumber())) {
				throw new IllegalArgumentException("For DEBIT, accountNumber must be your own account");
			}
			validateSufficientBalance(sourceAccount.getBalance(), amount);
			sourceAccount.setBalance(sourceAccount.getBalance() - amount);
			accountRepository.save(sourceAccount);
			break;

		case "CREDIT":
			if (!sourceAccount.getAccountNumber().equals(dto.getAccountNumber())) {
				throw new IllegalArgumentException("For CREDIT, accountNumber must be your own account");
			}
			sourceAccount.setBalance(sourceAccount.getBalance() + amount);
			accountRepository.save(sourceAccount);
			break;

		case "TRANSFER":
			Account targetAccount = accountRepository.findByAccountNumber(dto.getAccountNumber()).orElseThrow(
					() -> new ResourceNotFoundException("Receiver account not found: " + dto.getAccountNumber()));

			if (sourceAccount.getAccountNumber().equals(targetAccount.getAccountNumber())) {
				throw new IllegalArgumentException("Cannot transfer to the same account");
			}

			validateSufficientBalance(sourceAccount.getBalance(), amount);

			sourceAccount.setBalance(sourceAccount.getBalance() - amount);
			accountRepository.save(sourceAccount);

			targetAccount.setBalance(targetAccount.getBalance() + amount);
			accountRepository.save(targetAccount);

			Transaction transaction = new Transaction();
			transaction.setTransType(transType);
			transaction.setAmount(amount);
			transaction.setDate(LocalDate.now());
			transaction.setCustomer(customer);
			transaction.setAccount(sourceAccount);

			Transaction saved = transactionRepository.save(transaction);

			sendTransactionEmail(customer.getEmailid(), saved, sourceAccount.getBalance());

			return transactionMapper.toResponse(saved);

		default:
			throw new IllegalArgumentException("Invalid transaction type");
		}

		Transaction transaction = new Transaction();
		transaction.setTransType(transType);
		transaction.setAmount(amount);
		transaction.setDate(LocalDate.now());
		transaction.setCustomer(customer);
		transaction.setAccount(sourceAccount);

		Transaction saved = transactionRepository.save(transaction);

		sendTransactionEmail(customer.getEmailid(), saved, sourceAccount.getBalance());

		return transactionMapper.toResponse(saved);
	}

	private void validateSufficientBalance(double balance, double amount) {
		if ((balance - amount) < 500.0) {
			throw new IllegalArgumentException("Insufficient balance. Minimum ₹500 is required.");
		}
	}

	private void sendTransactionEmail(String toEmail, Transaction transaction, double currentBalance) {
		String subject = "Transaction Alert - " + transaction.getTransType();

		StringBuilder body = new StringBuilder();
		body.append("Dear Customer,\n\n");
		body.append("A new transaction has been made on your account.\n\n");
		body.append("Transaction Details:\n");
		body.append(" - Transaction ID: ").append(transaction.getTransId()).append("\n");
		body.append(" - Type: ").append(transaction.getTransType()).append("\n");
		body.append(" - Amount: ₹").append(transaction.getAmount()).append("\n");
		body.append(" - Current Balance: ₹").append(currentBalance).append("\n");
		body.append(" - Date: ").append(transaction.getDate()).append("\n");
		body.append(" - Account Number: ").append(transaction.getAccount().getAccountNumber()).append("\n");
		body.append("\nThank you for banking with us.");

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject(subject);
		message.setText(body.toString());

		mailSender.send(message);
	}

	@Override
	public List<TransactionResponseDTO> getTransactionsByAccountId(Long accountId) {
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("AccountId not found"));

		if (account.getBalance() == null) {
			throw new IllegalStateException("Account balance is null for accountId: " + accountId);
		}

		String loggedInUser = getLoggedInUserName();
		User loggedInUserEntity = userRepo.findByUserName(loggedInUser)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (hasRole(loggedInUserEntity, "ROLE_ADMIN")) {
			List<Transaction> transactions = account.getTransactions();
			if (transactions == null || transactions.isEmpty()) {
				throw new ResourceNotFoundException("No transactions assigned to this account.");
			}
			List<TransactionResponseDTO> transactionResponseDTOs = transactionMapper.toResponseList(transactions);
			for (TransactionResponseDTO responseDTO : transactionResponseDTOs) {
				responseDTO.setCurrentBalance(account.getBalance());
			}
			return transactionResponseDTOs;
		}

		String accountOwner = account.getCustomer().getUser().getUserName();
		if (!loggedInUser.equals(accountOwner)) {
			throw new IllegalArgumentException("You can only view transactions for your own account");
		}

		List<Transaction> transactions = account.getTransactions();
		if (transactions == null || transactions.isEmpty()) {
			throw new ResourceNotFoundException("No transactions assigned to this account.");
		}

		List<TransactionResponseDTO> transactionResponseDTOs = transactionMapper.toResponseList(transactions);
		for (TransactionResponseDTO responseDTO : transactionResponseDTOs) {
			responseDTO.setCurrentBalance(account.getBalance());
		}
		return transactionResponseDTOs;
	}

	@Override
	public List<TransactionResponseDTO> getTransactionsByCustomerId(Long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException("CustomerId not found"));

		String loggedInUser = getLoggedInUserName();
		User loggedInUserEntity = userRepo.findByUserName(loggedInUser)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (hasRole(loggedInUserEntity, "ROLE_ADMIN")) {
			List<Transaction> transactions = transactionRepository.findByCustomer_CustomerId(customerId);
			if (transactions == null || transactions.isEmpty()) {
				throw new ResourceNotFoundException("No transactions assigned to this customer.");
			}
			List<TransactionResponseDTO> transactionResponseDTOs = transactionMapper.toResponseList(transactions);
			if (customer.getAccounts() == null || customer.getAccounts().isEmpty()) {
				throw new IllegalStateException("Customer has no accounts.");
			}
			Account firstAccount = customer.getAccounts().get(0);
			if (firstAccount.getBalance() == null) {
				throw new IllegalStateException(
						"Account balance is null for accountId: " + firstAccount.getAccountId());
			}
			for (TransactionResponseDTO responseDTO : transactionResponseDTOs) {
				responseDTO.setCurrentBalance(firstAccount.getBalance());
			}
			return transactionResponseDTOs;
		}

		if (!loggedInUser.equals(customer.getUser().getUserName())) {
			throw new IllegalArgumentException("You can only view transactions for your own account");
		}

		List<Transaction> transactions = customer.getTransactions();
		if (transactions == null || transactions.isEmpty()) {
			throw new ResourceNotFoundException("No transactions assigned to this customer.");
		}

		List<TransactionResponseDTO> transactionResponseDTOs = transactionMapper.toResponseList(transactions);
		if (customer.getAccounts() == null || customer.getAccounts().isEmpty()) {
			throw new IllegalStateException("Customer has no accounts.");
		}
		Account firstAccount = customer.getAccounts().get(0);
		if (firstAccount.getBalance() == null) {
			throw new IllegalStateException("Account balance is null for accountId: " + firstAccount.getAccountId());
		}

		for (TransactionResponseDTO responseDTO : transactionResponseDTOs) {
			responseDTO.setCurrentBalance(firstAccount.getBalance());
		}
		return transactionResponseDTOs;
	}

	@Override
	public TransactionResponseDTO getTransactionById(Long id) {
		Transaction transaction = transactionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

		String loggedInUser = getLoggedInUserName();
		User loggedInUserEntity = userRepo.findByUserName(loggedInUser)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (hasRole(loggedInUserEntity, "ROLE_ADMIN")) {
			return transactionMapper.toResponse(transaction);
		}

		if (!loggedInUser.equals(transaction.getCustomer().getUser().getUserName())) {
			throw new IllegalArgumentException("You can only view your own transactions");
		}

		return transactionMapper.toResponse(transaction);
	}

	private String getLoggedInUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			return authentication.getName();
		}
		throw new IllegalStateException("User is not authenticated");
	}

}
