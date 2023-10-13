package com.koder.stock.client.dto;

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
public class StockHoldingDTO implements Serializable {

    private BigDecimal enableCount; // 可用金额
    private BigDecimal currentCount; //持仓
    private String stockCode;
    private BigDecimal costPrice; //成本价
    private BigDecimal keepCostPrice; //保本价
    private String exchangeType; //交易市场类型
    private BigDecimal marketValue; // 总价值
    private BigDecimal gapValue;// 持仓盈亏

}
