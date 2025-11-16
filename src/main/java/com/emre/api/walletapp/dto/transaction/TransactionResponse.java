package com.emre.api.walletapp.dto.transaction;

import com.emre.api.walletapp.enums.OppositePartyType;
import com.emre.api.walletapp.enums.TransactionStatus;
import com.emre.api.walletapp.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private Long walletId;
    private Double amount;
    private TransactionType type;
    private OppositePartyType oppositePartyType;
    private String oppositeParty;
    private TransactionStatus status;
    private String createdAt;
}