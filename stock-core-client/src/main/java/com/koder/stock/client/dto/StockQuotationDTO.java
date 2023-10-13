package com.koder.stock.client.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockQuotationDTO extends BaseDTO{

    private Long stockId;
    // 冗余
    private String code;

    private Instant quotationTime;
    private BigDecimal lastClosingPrice;
    private BigDecimal closingPrice;
    private BigDecimal openingPrice;
    private BigDecimal highestPrice;
    private BigDecimal lowestPrice;
    private BigDecimal latestPrice;
    private BigDecimal changeAmount;
    private BigDecimal changeRange;
    private BigDecimal turnOverRate;
    private BigDecimal turnOverVolume;
    private BigDecimal turnOverAmount;

}



