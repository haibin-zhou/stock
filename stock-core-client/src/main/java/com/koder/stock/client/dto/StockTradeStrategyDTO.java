package com.koder.stock.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StockTradeStrategyDTO extends BaseDTO{

    private Long id;
    private Long modifiedTime;
    private Long createTime;
    private Long userId;
    private Long stockId;
    /**
     * 有效，失效
     */
    private Integer status;
    /**
     * 动态基准价网格
     */
    private String strategyType;

    private Long startTime;
    private Long endTime;
    /**
     * 涨跌幅度，加仓或者补仓数量
     */
    private String strategyContent;

    private Long lastExecutionTime;
    private Integer executedCount;

    private BigDecimal lastEntrustPrice;
    private Integer LastEntrustCount;
    /**
     * 保底仓位
     */
    private Integer guaranteeCount;

}
