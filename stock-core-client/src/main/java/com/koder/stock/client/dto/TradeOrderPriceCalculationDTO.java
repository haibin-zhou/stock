package com.koder.stock.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TradeOrderPriceCalculationDTO {

    private String code;
    /**
     * 开盘价
     */
    private BigDecimal openPrice;
    /**
     * 上一次收盘价
     */
    private BigDecimal lastClosingPrice;
    /**
     * 当前价
     */
    private BigDecimal latestPrice;
    /**
     * 未完成订单的最低价
     */
    private BigDecimal orderLowestPrice;
    /**
     * 未完成订单的最高价
     */
    private BigDecimal orderHighestPrice;

}
