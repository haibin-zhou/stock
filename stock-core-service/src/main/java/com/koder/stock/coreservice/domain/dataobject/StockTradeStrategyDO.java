package com.koder.stock.coreservice.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StockTradeStrategyDO implements Serializable {

    private Long id;
    private Long modifiedTime;
    private Long createTime;
    private Long stockId;
    private Long userId;
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
    private String features;
}
