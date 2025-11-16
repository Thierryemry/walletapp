package com.emre.api.walletapp.service;

import com.emre.api.walletapp.dto.transaction.ApproveTransactionRequest;
import com.emre.api.walletapp.dto.transaction.DepositRequest;
import com.emre.api.walletapp.dto.transaction.TransactionResponse;
import com.emre.api.walletapp.dto.transaction.WithdrawRequest;

import java.util.List;

public interface TransactionService {

    TransactionResponse deposit(Long walletId, DepositRequest request);

    TransactionResponse withdraw(Long walletId, WithdrawRequest request);

    List<TransactionResponse> listTransactions(Long walletId);

    TransactionResponse approveOrDeny(ApproveTransactionRequest request);
}
