package com.koder.stock.coreservice.domain.constant;

import com.google.common.collect.ImmutableMap;

public class StockExchangeTypeConstant {

    public final static String HK_EXCHANGE_TYPE = "K";
    public final static String US_EXCHANGE_TYPE = "P";

    public final static ImmutableMap<Integer, String> exchangeTypeMap = ImmutableMap.of(10000, "K", 20000, "P");

    public static String getExchangeType(Integer market){
        return exchangeTypeMap.get(market);
    }
}
