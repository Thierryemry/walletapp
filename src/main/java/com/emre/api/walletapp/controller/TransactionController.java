package com.emre.api.walletapp.controller;

import com.emre.api.walletapp.dto.transaction.ApproveTransactionRequest;
import com.emre.api.walletapp.dto.transaction.DepositRequest;
import com.emre.api.walletapp.dto.transaction.TransactionResponse;
import com.emre.api.walletapp.dto.transaction.WithdrawRequest;
import com.emre.api.walletapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/wallet/{walletId}/deposit")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('EMPLOYEE')")
    public TransactionResponse deposit(@PathVariable Long walletId,
                                       @RequestBody DepositRequest request) {
        return transactionService.deposit(walletId, request);
    }

    @PostMapping("/wallet/{walletId}/withdraw")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('EMPLOYEE')")
    public TransactionResponse withdraw(@PathVariable Long walletId,
                                        @RequestBody WithdrawRequest request) {
        return transactionService.withdraw(walletId, request);
    }

    @GetMapping("/wallet/{walletId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('EMPLOYEE')")
    public List<TransactionResponse> listTransactions(@PathVariable Long walletId) {
        return transactionService.listTransactions(walletId);
    }

    @PostMapping("/approveOrDeny")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public TransactionResponse approveTransaction(@RequestBody ApproveTransactionRequest request) {
        return transactionService.approveOrDeny(request);
    }
}
