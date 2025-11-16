package com.emre.api.walletapp.service;



import com.emre.api.walletapp.dto.wallet.CreateWalletRequest;
import com.emre.api.walletapp.dto.wallet.WalletResponse;

import java.util.List;

public interface WalletService {

    WalletResponse createWalletByEmployee(CreateWalletRequest request, Long customerId);

    WalletResponse createWalletByCustomer(CreateWalletRequest request);

    List<WalletResponse> listWalletsByEmployee(Long customerId);

    List<WalletResponse> listWalletsByCustomer();
}
