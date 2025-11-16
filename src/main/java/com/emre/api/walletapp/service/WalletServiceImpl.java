package com.emre.api.walletapp.service;

import com.emre.api.walletapp.dto.wallet.CreateWalletRequest;
import com.emre.api.walletapp.dto.wallet.WalletResponse;
import com.emre.api.walletapp.exception.ResourceNotFoundException;
import com.emre.api.walletapp.exception.UnauthorizedAccessException;
import com.emre.api.walletapp.model.AppUser;
import com.emre.api.walletapp.model.Wallet;
import com.emre.api.walletapp.repository.WalletRepository;
import com.emre.api.walletapp.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public WalletResponse createWalletByEmployee(CreateWalletRequest request, Long customerId) {
        AppUser currentUser = SecurityUtils.getCurrentUser();

        if (!currentUser.getRole().equals("EMPLOYEE")) {
            throw new UnauthorizedAccessException("Only employees can create wallet for another person");
        }

        Wallet wallet = Wallet.builder()
                .customerId(customerId)
                .walletName(request.getWalletName())
                .currency(request.getCurrency())
                .activeForShopping(request.isActiveForShopping())
                .activeForWithdraw(request.isActiveForWithdraw())
                .balance(0.0)
                .usableBalance(0.0)
                .build();

        Wallet saved = null;

        try {
            saved = walletRepository.save(wallet);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not create wallet");
        }

        return mapToDto(saved);
    }


    @Override
    public WalletResponse createWalletByCustomer(CreateWalletRequest request) {
        AppUser currentUser = SecurityUtils.getCurrentUser();

        if (!currentUser.getRole().equals("CUSTOMER")) {
            throw new UnauthorizedAccessException("Request was made by inappropriate user");
        }

        Wallet wallet = Wallet.builder()
                .customerId(currentUser.getId())
                .walletName(request.getWalletName())
                .currency(request.getCurrency())
                .activeForShopping(request.isActiveForShopping())
                .activeForWithdraw(request.isActiveForWithdraw())
                .balance(0.0)
                .usableBalance(0.0)
                .build();

        Wallet saved = null;

        try {
            saved = walletRepository.save(wallet);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not create wallet");
        }

        return mapToDto(saved);
    }

    @Override
    public List<WalletResponse> listWalletsByEmployee(Long customerId) {
        var currentUser = SecurityUtils.getCurrentUser();

        if (!currentUser.getRole().equals("EMPLOYEE")) {
            throw new UnauthorizedAccessException("Only employees can list wallet for another person");
        }

        List<Wallet> wallets;

        try {
            wallets = walletRepository.findByCustomerId(customerId);
        }

        catch (Exception e) {
            throw new RuntimeException("Could not read wallets from database");
        }

        if (wallets.isEmpty()) {
            throw new ResourceNotFoundException("No wallet was found belong to customer");
        }

        return wallets.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<WalletResponse> listWalletsByCustomer() {
        var currentUser = SecurityUtils.getCurrentUser();

        if (!currentUser.getRole().equals("CUSTOMER")) {
            throw new UnauthorizedAccessException("Request was made by inappropriate user");
        }

        List<Wallet> wallets;

        try {
            wallets = walletRepository.findByCustomerId(currentUser.getId());
        }

        catch (Exception e) {
            throw new RuntimeException("Could not read wallets from database");
        }

        if (wallets.isEmpty()) {
            throw new ResourceNotFoundException("No wallet was found belong to customer");
        }

        return wallets.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private WalletResponse mapToDto(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .customerId(wallet.getCustomerId())
                .walletName(wallet.getWalletName())
                .currency(wallet.getCurrency())
                .activeForShopping(wallet.isActiveForShopping())
                .activeForWithdraw(wallet.isActiveForWithdraw())
                .balance(wallet.getBalance())
                .usableBalance(wallet.getUsableBalance())
                .build();
    }
}