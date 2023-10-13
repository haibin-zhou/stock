package com.koder.stock.coreservice.domain.constant;

public class StockEntrustTypeConstant {
    /**
     * 港股：'0'-竞价限价、'1'-竞价、'2'-增强限价盘、'3'-限价盘、'4'-特别限价盘、'6'-暗盘、'7'-碎股
     * 美股：'3'-限价盘、'5'-市价盘、'8'-冰山市价、'9'-冰山限价、'10'-隐藏市价、'11'-隐藏限价
     * A股：'3'-限价盘
     * 条件单：'31'-止盈限价单、'32'-止盈市价单(美股)、'33'-止损限价单、'34'-止损市价单(美股)、'35'-追踪止损限价单、'36'-追踪止损市价单(美股)
     */
    public static final String LIMIT_PRICE = "3";

}
