package com.emre.api.walletapp.service;

import com.emre.api.walletapp.dto.transaction.ApproveTransactionRequest;
import com.emre.api.walletapp.dto.transaction.DepositRequest;
import com.emre.api.walletapp.dto.transaction.TransactionResponse;
import com.emre.api.walletapp.dto.transaction.WithdrawRequest;
import com.emre.api.walletapp.enums.OppositePartyType;
import com.emre.api.walletapp.enums.TransactionStatus;
import com.emre.api.walletapp.enums.TransactionType;
import com.emre.api.walletapp.exception.ResourceNotFoundException;
import com.emre.api.walletapp.exception.UnauthorizedAccessException;
import com.emre.api.walletapp.model.Transaction;
import com.emre.api.walletapp.model.Wallet;
import com.emre.api.walletapp.repository.TransactionRepository;
import com.emre.api.walletapp.repository.WalletRepository;
import com.emre.api.walletapp.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    @Override
    public TransactionResponse deposit(Long walletId, DepositRequest request) {
        var currentUser = SecurityUtils.getCurrentUser();
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (currentUser.getRole().equals("CUSTOMER") && !wallet.getCustomerId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("Customer can only deposit to own wallet");
        }

        TransactionStatus status = request.getAmount() > 1000 ? TransactionStatus.PENDING : TransactionStatus.APPROVED;

        Transaction transaction = Transaction.builder()
                .walletId(walletId)
                .amount(request.getAmount())
                .type(TransactionType.DEPOSIT)
                .oppositePartyType(OppositePartyType.PAYMENT)
                .oppositeParty(request.getSource())
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        if (status == TransactionStatus.APPROVED) {
            wallet.setBalance(wallet.getBalance() + request.getAmount());
            wallet.setUsableBalance(wallet.getUsableBalance() + request.getAmount());
        } else {
            wallet.setBalance(wallet.getBalance() + request.getAmount());
        }
        walletRepository.save(wallet);

        return mapToDto(saved);
    }

    @Override
    public TransactionResponse withdraw(Long walletId, WithdrawRequest request) {
        var currentUser = SecurityUtils.getCurrentUser();
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (currentUser.getRole().equals("CUSTOMER") && !wallet.getCustomerId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("Customer can only withdraw from own wallet");
        }

        if (!wallet.isActiveForWithdraw()) {
            throw new UnauthorizedAccessException("Wallet is not active for withdrawal");
        }

        if (request.getAmount() > wallet.getUsableBalance()) {
            throw new UnauthorizedAccessException("Insufficient usable balance");
        }

        TransactionStatus status = request.getAmount() > 1000 ? TransactionStatus.PENDING : TransactionStatus.APPROVED;

        Transaction transaction = Transaction.builder()
                .walletId(walletId)
                .amount(request.getAmount())
                .type(TransactionType.WITHDRAW)
                .oppositePartyType(OppositePartyType.PAYMENT)
                .oppositeParty(request.getDestination())
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        if (status == TransactionStatus.APPROVED) {
            wallet.setBalance(wallet.getBalance() - request.getAmount());
            wallet.setUsableBalance(wallet.getUsableBalance() - request.getAmount());
        } else {
            wallet.setUsableBalance(wallet.getUsableBalance() - request.getAmount());
        }
        walletRepository.save(wallet);

        return mapToDto(saved);
    }

    @Override
    public List<TransactionResponse> listTransactions(Long walletId) {
        var currentUser = SecurityUtils.getCurrentUser();
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (currentUser.getRole().equals("CUSTOMER") && !wallet.getCustomerId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("Customer can only view own wallet transactions");
        }

        return transactionRepository.findByWalletId(walletId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse approveOrDeny(ApproveTransactionRequest request) {

        var currentUser = SecurityUtils.getCurrentUser();

        if (!currentUser.getRole().equals("EMPLOYEE")) {
            throw new UnauthorizedAccessException("Only employees can approve/deny transactions");
        }

        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (transaction.getStatus() == TransactionStatus.APPROVED) {
            throw new RuntimeException("Transaction is already approved");
        }

        Wallet wallet = walletRepository.findById(transaction.getWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        transaction.setStatus(request.getStatus());
        transactionRepository.save(transaction);

        if (request.getStatus() == TransactionStatus.APPROVED) {
            if (transaction.getType() == TransactionType.DEPOSIT) {
                wallet.setUsableBalance(wallet.getUsableBalance() + transaction.getAmount());
            } else if (transaction.getType() == TransactionType.WITHDRAW) {
                wallet.setBalance(wallet.getBalance() - transaction.getAmount());
            }
        } else if (request.getStatus() == TransactionStatus.DENIED) {
            if (transaction.getType() == TransactionType.WITHDRAW) {
                wallet.setUsableBalance(wallet.getUsableBalance() + transaction.getAmount());
            } else if (transaction.getType() == TransactionType.DEPOSIT) {
                wallet.setBalance(wallet.getBalance() - transaction.getAmount());
            }
        }

        walletRepository.save(wallet);
        return mapToDto(transaction);
    }

    private TransactionResponse mapToDto(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .walletId(transaction.getWalletId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .oppositePartyType(transaction.getOppositePartyType())
                .oppositeParty(transaction.getOppositeParty())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt().toString())
                .build();
    }
}
