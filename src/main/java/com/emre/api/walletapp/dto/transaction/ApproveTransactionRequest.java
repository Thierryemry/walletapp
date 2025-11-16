package com.emre.api.walletapp.dto.transaction;

import com.emre.api.walletapp.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApproveTransactionRequest {
    private Long transactionId;
    private TransactionStatus status;
}
