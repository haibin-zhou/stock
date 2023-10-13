package com.koder.stock.coreservice.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockUserFundDO implements Serializable {

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
