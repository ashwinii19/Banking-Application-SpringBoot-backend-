package com.aurionpro.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.dto.PassbookRequestDTO;
import com.aurionpro.dto.PassbookResponseDTO;
import com.aurionpro.entity.Account;
import com.aurionpro.entity.Transaction;
import com.aurionpro.exception.ResourceNotFoundException;
import com.aurionpro.repository.AccountRepository;
import com.aurionpro.repository.TransactionRepository;
import com.aurionpro.service.PassbookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/passbook")
public class PassbookController {

    @Autowired
    private PassbookService passbookService;
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/view")
    public ResponseEntity<String> viewPassbookPage(@RequestParam String accountNumber,
                                                   @RequestParam(defaultValue = "0") int page) {
        int pageSize = 5;

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        List<Transaction> allTransactions = new ArrayList<>(account.getTransactions());
        allTransactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        int totalPages = (int) Math.ceil((double) allTransactions.size() / pageSize);

        if (page >= totalPages) {
            return ResponseEntity.badRequest().body("Page not found");
        }

        int start = page * pageSize;
        int end = Math.min(start + pageSize, allTransactions.size());
        List<Transaction> pageTransactions = allTransactions.subList(start, end);

        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h2>Passbook - Page ").append(page + 1).append(" of ").append(totalPages).append("</h2>");
        html.append("<table border='1'><tr><th>ID</th><th>Type</th><th>Amount</th><th>Date</th></tr>");

        for (Transaction tx : pageTransactions) {
            html.append("<tr>")
                .append("<td>").append(tx.getTransId()).append("</td>")
                .append("<td>").append(tx.getTransType()).append("</td>")
                .append("<td>").append(tx.getAmount()).append("</td>")
                .append("<td>").append(tx.getDate()).append("</td>")
                .append("</tr>");
        }

        html.append("</table><br/>");

        html.append("<p>Go to Page: ");
        for (int i = 0; i < totalPages; i++) {
            html.append("<a href='/api/passbook/view?accountNumber=")
                .append(accountNumber)
                .append("&page=").append(i)
                .append("'>")
                .append(i + 1)
                .append("</a> ");
        }
        html.append("</p>");

        html.append("</body></html>");
        return ResponseEntity.ok().body(html.toString());
    }

    @PostMapping("/generate")
    public ResponseEntity<PassbookResponseDTO> generatePassbook(@Valid @RequestBody PassbookRequestDTO requestDTO) {
        PassbookResponseDTO response = passbookService.generateAndSendPassbook(requestDTO);
        return ResponseEntity.ok(response);
    }
}



