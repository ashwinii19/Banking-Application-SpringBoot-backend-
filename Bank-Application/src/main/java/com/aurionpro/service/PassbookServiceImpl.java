package com.aurionpro.service;

import com.aurionpro.dto.PassbookRequestDTO;
import com.aurionpro.dto.PassbookResponseDTO;
import com.aurionpro.entity.Account;
import com.aurionpro.entity.Customer;
import com.aurionpro.entity.Passbook;
import com.aurionpro.entity.Transaction;
import com.aurionpro.exception.ResourceNotFoundException;
import com.aurionpro.mapping.PassbookMapper;
import com.aurionpro.repository.AccountRepository;
import com.aurionpro.repository.PassbookRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PassbookServiceImpl implements PassbookService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PassbookRepository passbookRepository; 

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PassbookMapper passbookMapper;

    @Override
    public PassbookResponseDTO generateAndSendPassbook(PassbookRequestDTO requestDTO) {
        Account account = accountRepository.findByAccountNumber(requestDTO.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with account number: " + requestDTO.getAccountNumber()));

        Customer customer = account.getCustomer();
        if (customer == null) {
            throw new ResourceNotFoundException("Customer not found for the account number: " + requestDTO.getAccountNumber());
        }

        List<Transaction> allTransactions = new ArrayList<>();
        if (account.getTransactions() != null) {
            allTransactions.addAll(account.getTransactions());
        }

        if (allTransactions.isEmpty()) {
            throw new ResourceNotFoundException("No transactions found for this account");
        }

        allTransactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        Transaction latestTransaction = allTransactions.get(0);

        Passbook passbook = new Passbook();
        passbook.setAccountId(account.getAccountId());
        passbook.setAccountNumber(account.getAccountNumber());
        passbook.setBalance(account.getBalance());
        passbook.setTransId(latestTransaction.getTransId());
        passbook.setTransType(latestTransaction.getTransType());
        passbook.setDate(latestTransaction.getDate());

        passbookRepository.save(passbook);

        PassbookResponseDTO responseDTO = passbookMapper.toResponse(passbook, latestTransaction);

        if (customer.getEmailid() != null && !customer.getEmailid().isEmpty()) {
            sendPassbookEmail(customer.getEmailid(), responseDTO, allTransactions);
        }

        return responseDTO;
    }
    
    private void sendPassbookEmail(String toEmail, PassbookResponseDTO passbookDTO, List<Transaction> allTransactions) {
        int pageSize = 5;
        int totalTransactions = allTransactions.size();
        int totalPages = (int) Math.ceil((double) totalTransactions / pageSize);

        for (int page = 0; page < totalPages; page++) {
            int start = page * pageSize;
            int end = Math.min(start + pageSize, totalTransactions);
            List<Transaction> pageTransactions = allTransactions.subList(start, end);

            String subject = "Your Passbook Details - Page " + (page + 1) + " of " + totalPages;

            StringBuilder body = new StringBuilder();
            body.append("<html><body>");
            body.append("<p>Dear Customer,</p>");
            body.append("<p>Account Number: ").append(passbookDTO.getAccountNumber()).append("</p>");
            body.append("<p>Current Balance: Rs").append(passbookDTO.getCurrentBalance()).append("</p>");
            body.append("<p>Transactions (Page ").append(page + 1).append(" of ").append(totalPages).append("):</p>");

            body.append("<table border='1' cellpadding='5' cellspacing='0'>");
            body.append("<tr>")
                .append("<th>Transaction ID</th>")
                .append("<th>Type</th>")
                .append("<th>Amount (Rs)</th>")
                .append("<th>Date</th>")
                .append("</tr>");

            for (Transaction transaction : pageTransactions) {
                body.append("<tr>")
                    .append("<td>").append(transaction.getTransId()).append("</td>")
                    .append("<td>").append(transaction.getTransType()).append("</td>")
                    .append("<td>").append(transaction.getAmount()).append("</td>")
                    .append("<td>").append(transaction.getDate()).append("</td>")
                    .append("</tr>");
            }

            body.append("</table>");

            body.append("<p style='margin-top: 20px;'>Go to Page: ");
            for (int i = 0; i < totalPages; i++) {
                int pageNum = i + 1;
                body.append("<a href='http://localhost:8080/api/passbook/view?accountNumber=")
                    .append(passbookDTO.getAccountNumber())
                    .append("&page=").append(i)
                    .append("'>")
                    .append(pageNum)
                    .append("</a> ");
            }
            body.append("</p>");

            body.append("<p>Thank you for banking with us.</p>");
            body.append("</body></html>");

            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(toEmail);
                helper.setSubject(subject);
                helper.setText(body.toString(), true); 
                mailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }



}
