package com.aurionpro.service;

import java.util.List;
import java.util.stream.Collectors;

import com.aurionpro.dto.AccountRequestDTO;
import com.aurionpro.dto.AccountResponseDTO;
import com.aurionpro.dto.AccountUpdateDTO;
import com.aurionpro.entity.Account;
import com.aurionpro.entity.Customer;
import com.aurionpro.exception.ResourceNotFoundException;
import com.aurionpro.mapping.AccountMapper;
import com.aurionpro.repository.AccountRepository;
import com.aurionpro.repository.CustomerRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public AccountServiceImpl(AccountMapper accountMapper,
                               AccountRepository accountRepository,
                               CustomerRepository customerRepository,
                               JavaMailSender mailSender) {
        this.accountMapper = accountMapper;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.mailSender = mailSender;
    }

    private String getLoggedInUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean hasRole(String roleName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + roleName));
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%012d", (long) (Math.random() * 1_000_000_000_000L));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    @Override
    public AccountResponseDTO createAccount(AccountRequestDTO accreqdto) {
        if (!hasRole("CUSTOMER")) {
            throw new IllegalArgumentException("Only customers can create accounts");
        }

        if (accreqdto.getCustomerId() == null) {
            throw new IllegalArgumentException("CustomerId cannot be null");
        }

        String loggedInUser = getLoggedInUserName();
        Customer customer = customerRepository.findById(accreqdto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (!customer.getUser().getUserName().equals(loggedInUser)) {
            throw new IllegalArgumentException("Customers can only create accounts for themselves");
        }

        Account account = accountMapper.toEntity(accreqdto);
        account.setCustomer(customer);
        account.setIsAccountDeleted("N");
        account.setAccountNumber(generateUniqueAccountNumber());

        Account saved = accountRepository.save(account);
        sendAccountCreationEmail(customer, saved);
        return accountMapper.toResponse(saved);
    }

    @Override
    public AccountResponseDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!"N".equalsIgnoreCase(account.getIsAccountDeleted())) {
            throw new ResourceNotFoundException("Account is deleted");
        }

        if (hasRole("ADMIN")) {
            return accountMapper.toResponse(account);
        }

        String loggedInUser = getLoggedInUserName();
        String accountOwner = account.getCustomer().getUser().getUserName();

        if (!loggedInUser.equals(accountOwner)) {
            throw new IllegalArgumentException("You can only view your own accounts");
        }

        return accountMapper.toResponse(account);
    }

    @Override
    public List<AccountResponseDTO> getAllAccounts() {
        if (!hasRole("ADMIN")) {
            throw new IllegalArgumentException("Only admins can view all accounts");
        }

        List<Account> accounts = accountRepository.findByIsAccountDeleted("N");
        return accountMapper.toResponseList(accounts);
    }

    @Override
    public AccountResponseDTO updateAccount(Long id, AccountUpdateDTO accupdto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!"N".equalsIgnoreCase(account.getIsAccountDeleted())) {
            throw new IllegalStateException("Cannot update a deleted account");
        }

        String loggedInUser = getLoggedInUserName();
        String accountOwner = account.getCustomer().getUser().getUserName();

        if (!hasRole("CUSTOMER") || !loggedInUser.equals(accountOwner)) {
            throw new IllegalArgumentException("You can only update your own account");
        }

        accountMapper.applyUpdate(accupdto, account);
        Account updated = accountRepository.save(account);
        return accountMapper.toResponse(updated);
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not Found"));

        if (!"N".equalsIgnoreCase(account.getIsAccountDeleted())) {
            throw new IllegalStateException("Account already deleted");
        }

        String loggedInUser = getLoggedInUserName();
        String accountOwner = account.getCustomer().getUser().getUserName();

        if (hasRole("ADMIN") || (hasRole("CUSTOMER") && loggedInUser.equals(accountOwner))) {
            account.setIsAccountDeleted("Y");
            accountRepository.save(account);
            sendAccountDeletionEmail(account.getCustomer(), account);
        } else {
            throw new IllegalArgumentException("Not authorized to delete this account");
        }
    }

    @Override
    public List<AccountResponseDTO> getAccountsByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        String loggedInUser = getLoggedInUserName();
        boolean isAdmin = hasRole("ADMIN");

        if (!isAdmin && !loggedInUser.equals(customer.getUser().getUserName())) {
            throw new IllegalArgumentException("You can only view your own accounts");
        }

        List<Account> accounts = accountRepository.findByCustomerCustomerIdAndIsAccountDeleted(customerId, "N");
        return accounts.stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void sendAccountCreationEmail(Customer customer, Account account) {
        if (customer.getEmailid() == null || customer.getEmailid().isEmpty()) return;

        String subject = "Account Created Successfully";
        StringBuilder body = new StringBuilder();
        body.append("Dear Customer,\n\n");
        body.append("Your new bank account has been successfully created.\n\n");
        body.append("Account Details:\n");
        body.append(" - Account Number: ").append(account.getAccountNumber()).append("\n");
        body.append(" - Balance: ₹").append(account.getBalance()).append("\n\n");
        body.append("Thank you for choosing our bank.\n");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(customer.getEmailid());
        message.setSubject(subject);
        message.setText(body.toString());
        mailSender.send(message);
    }

    private void sendAccountDeletionEmail(Customer customer, Account account) {
        if (customer.getEmailid() == null || customer.getEmailid().isEmpty()) return;

        String subject = "Account Deleted Successfully";
        StringBuilder body = new StringBuilder();
        body.append("Dear Customer,\n\n");
        body.append("Your bank account has been successfully deleted.\n\n");
        body.append("Deleted Account Details:\n");
        body.append(" - Account Number: ").append(account.getAccountNumber()).append("\n\n");
        body.append("Thank you for banking with us.\n");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(customer.getEmailid());
        message.setSubject(subject);
        message.setText(body.toString());
        mailSender.send(message);
    }

    @Transactional
    public void createAccountWithCustomer(Account account, Customer customer) {
        customerRepository.save(customer);
        account.setCustomer(customer);
        account.setIsAccountDeleted("N");
        accountRepository.save(account);
    }
}
