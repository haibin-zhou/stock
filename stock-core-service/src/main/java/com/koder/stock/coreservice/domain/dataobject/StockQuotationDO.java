package com.koder.stock.coreservice.domain.dataobject;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockQuotationDO implements Serializable {

    private Long id;
    private Long modifiedTime;
    private Long createTime;
    private Long stockId;
    // 冗余
    private String code;

    private Long quotationTime;
    private BigDecimal lastClosingPrice;
    private BigDecimal closingPrice;
    private BigDecimal openingPrice;
    private BigDecimal highestPrice;
    private BigDecimal lowestPrice;
    private BigDecimal changeAmount;
    private BigDecimal changeRange;
    private BigDecimal turnOverRate;
    private BigDecimal turnOverVolume;
    private BigDecimal turnOverAmount;

    private String features;
}
