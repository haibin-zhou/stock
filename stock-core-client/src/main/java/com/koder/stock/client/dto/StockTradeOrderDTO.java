package com.koder.stock.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockTradeOrderDTO extends BaseDTO implements Delayed {

    private Long id;
    private Long modifiedTime;
    private Long createTime;
    private Long userId;
    private String tradeOrderNo; // 委托编号
    private String stockCode; // 股票代码（交易接口：港股市场需要带后缀 .HK）
    private String exchangeType; // 'K'-港股、'P'-美股、'v'-深股通、't'-沪股通
    private BigDecimal entrustPrice; // 委托价格
    private int entrustVolume; // 委托数量
    private Long strategyId;
    /**
     * '1'-买入、'2'-卖出
     */
    private String entrustBs;
    //港股：'0'-竞价限价、'1'-竞价、'2'-增强限价盘、'3'-限价盘、'4'-特别限价盘、'6'-暗盘
    //美股：'3'-限价盘、'5'-市价盘、'8'-冰山市价、'9'-冰山限价、'10'-隐藏市价、'11'-隐藏限价
    //A股：'3'-限价盘
    //条件单：'31'-止盈限价单、'32'-止盈市价单(美股)、'33'-止损限价单、'34'-止损市价单(美股)、'35'-追踪止损限价单、'36'-追踪止损市价单(美股)
    private String entrustType;
    // exchange 交易所 SMART AMEX ARCA BATS BEX BYX CBOE CHX DRCTEDGE EDGEA EDGX IBKRTS IEX ISE ISLAND LTSE MEMX NYSE NYSENAT PEARL PHLX PSX
    private String exchange;
    // 0:否 1:是 3:只支持盘中 5:港股支持盘中及暗盘 7:美股支持盘中及盘前盘后
    private String tradeWhenClose;
    // 如为冰山单，该值必填，且该值必须大于0，小于等于委托数量
    private String iceBergDisplaySize;
    // 条件单
    private String strategyType;
    //    // 条件单
//    private Integer validDays;
//    private String condValue;
//    private String condTraceType;
    // ENTRUSTED / ALL_SUCCESS / PART_SUCCESS / FAILED / Cancelling / Cancelled
    private String status;
    /**
     * outer trade no
     */
    private String outerTradeNo;

    private BigDecimal dealPrice;
    private Integer dealCount;
    private Long dealDateTime;

    //0：不重建，1:需重建 2:已重建
    private String reCreate;

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.createTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        if (other == this) // compare zero ONLY if same object
            return 0;
        if (other instanceof StockTradeOrderDTO) {
            StockTradeOrderDTO x = (StockTradeOrderDTO) other;
            long diff = this.createTime - x.createTime;
            if (diff < 0)
                return -1;
            else if (diff > 0)
                return 1;
            else
                return 1;
        }
        long d = (getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS));
        return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
    }
}
