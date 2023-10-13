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
public class StockDealOrderDO implements Serializable {

    private Long id;
    private Long modifiedTime;
    private Long createTime;
    private Long tradeOrderId;//唯一交易ID
    private String stockCode; //股票代码
    private BigDecimal dealPrice; //成交价格
    private String entrustBs; // 委托方向 1:买入、2:卖出
    private BigDecimal dealAmount; //成交金额
    private Integer entrustCount; //委托数量
    private Integer dealCount; //成交数量
    private String date; //如果是成交接口，成交日期
    private String dealTime; //成交时间
    private String entrustTime; //委托时间
    private String status; //委托状态
    private String tradeOrderNo; //委托编号
    private BigDecimal unDealAmount; //未成交数量
    private String entrustType;//委托类型
    private String opponentSeat; //对手席位号，仅当日成交有该字段
    private String features; //备注信息
    private String exchangeType; //交易类型
    private String exchange; //交易所


}
