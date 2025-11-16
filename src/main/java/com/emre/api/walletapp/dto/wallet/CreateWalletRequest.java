package com.emre.api.walletapp.dto.wallet;


import com.emre.api.walletapp.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateWalletRequest {
    private String walletName;
    private Currency currency; // TRY, USD, EUR
    private boolean activeForShopping;
    private boolean activeForWithdraw;
}
