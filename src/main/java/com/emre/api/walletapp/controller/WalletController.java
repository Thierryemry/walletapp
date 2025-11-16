package com.emre.api.walletapp.controller;

import com.emre.api.walletapp.dto.wallet.CreateWalletRequest;
import com.emre.api.walletapp.dto.wallet.WalletResponse;
import com.emre.api.walletapp.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<WalletResponse> listWalletByCustomer() {
        return walletService.listWalletsByCustomer();
    }

    @GetMapping("/list/{customerId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<WalletResponse> listWalletsByEmployee(@PathVariable Long customerId) {
        return walletService.listWalletsByEmployee(customerId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER')")
    public WalletResponse createWalletByCustomer(@RequestBody CreateWalletRequest request) {
        return walletService.createWalletByCustomer(request);
    }

    @PostMapping("/create/{customerId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public WalletResponse createWalletByEmployee(@PathVariable Long customerId,
                                                 @RequestBody CreateWalletRequest request) {
        return walletService.createWalletByEmployee(request, customerId);
    }
}
