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
public class StockTradeOrderDO implements Serializable {

    private Long id;
    private Long modifiedTime;
    private Long createTime;
    private Long userId;
    private String tradeOrderNo; // 委托编号
    private String stockCode; // 股票代码（交易接口：港股市场需要带后缀 .HK）
    private String exchangeType; // 'K'-港股、'P'-美股、'v'-深股通、't'-沪股通
    private BigDecimal entrustPrice; // 委托价格
    private Integer entrustCount;// 委托数量
    /**
     * '1'-多头开仓、'2'-多头平仓、'3'-空头平仓、'4'-空头开仓
     * @see com.koder.stock.coreservice.domain.constant.StockEntrustBS
     */
    private String entrustBs;
    //港股：'0'-竞价限价、'1'-竞价、'2'-增强限价盘、'3'-限价盘、'4'-特别限价盘、'6'-暗盘
    //美股：'3'-限价盘、'5'-市价盘、'8'-冰山市价、'9'-冰山限价、'10'-隐藏市价、'11'-隐藏限价
    //A股：'3'-限价盘
    //条件单：'31'-止盈限价单、'32'-止盈市价单(美股)、'33'-止损限价单、'34'-止损市价单(美股)、'35'-追踪止损限价单、'36'-追踪止损市价单(美股)
    private String entrustType;
    private String exchange;
    // 0:否 1:是 3:只支持盘中 5:港股支持盘中及暗盘 7:美股支持盘中及盘前盘后
    private String tradeWhenClose;
    // 如为冰山单，该值必填，且该值必须大于0，小于等于委托数量
    private String iceBergDisplaySize;
    // 条件单
    private String strategyType;
    // 策略ID
    private Long strategyId;
    // 订单扩展
    private String features;
//    // 条件单
//    private Integer validDays;
//    private String condValue;
//    private String condTraceType;
    // ENTRUSTED / ALL_SUCCESS / PART_SUCCESS / FAILED / Cancelling / Cancelled
    /**
     * 0	No Register(未报)
     * 1	Wait to Register(待报)
     * 2	Host Registered(已报)
     * 3	Wait for Cancel(已报待撤)
     * 4	Wait for Cancel(Partially Matched)(部成待撤)
     * 5	Partially Cancelled(部撤)
     * 6	Cancelled(已撤)
     * 7	Partially Filled(部成)
     * 8	Filled(已成)
     * 9	Host Reject(废单)
     * A	Wait for Modify (Registed)（已报待改）
     * B	Unregistered（无用）
     * C	Registering（无用）
     * D	Revoke Cancel（无用）
     * W	Wait for Confirming（待确认）
     * X	Pre Filled（无用）
     * E	Wait for Modify (Partially Matched)（部成待改）
     * F	Reject（预埋单检查废单）
     * G	Cancelled(Pre-Order)（预埋单已撤）
     * H	Wait for Review（待审核）
     * J	Review Fail（审核失败）
     * */
    private String status;
    /**
     * outer trade no
     */
    private String outerTradeNo;

    private BigDecimal dealPrice;
    private Integer dealCount;
    private Long dealDateTime;

    //0：不重建，1:需重建 2:已重建 3:取消重建
    private String reCreate;
}
