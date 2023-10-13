package com.koder.stock.coreservice.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StockUserHoldingDO implements Serializable {

    private Long id;
    private Long modifiedTime;
    private Long createTime;
    private Long userId;
    private String stockName;
    private BigDecimal enableCount;
    private BigDecimal holdingCount;
    private String stockCode;
    private BigDecimal costPrice;
    private BigDecimal theDayCostPrice;
    private BigDecimal theDayIncomeAmount;
    private BigDecimal theDayOutAmount;
    private BigDecimal breakevenPrice;
    private String exchangeType;








}
