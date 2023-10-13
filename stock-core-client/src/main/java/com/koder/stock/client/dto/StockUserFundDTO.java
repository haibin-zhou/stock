package com.koder.stock.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUserFundDTO implements Serializable {
    private Long id;
    private Long modifiedTime;
    private Long createTime;
    private Long userId;
    private String exchangeType;
    private BigDecimal assetBalance;
    private BigDecimal availableBalance;
    private BigDecimal holdingBalance;
    private Long assetDate;
}
