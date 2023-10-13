package com.koder.stock.coreservice.domain.constant;

import java.util.HashMap;
import java.util.Map;

public class StockBasicMarket {

    public static int HK_MARKET = 10000;
    public static int US_MARKET = 20000;

    private static Map<Integer,String> marketMap = new HashMap<>();

    static {
        marketMap.put(HK_MARKET,"港股");
        marketMap.put(US_MARKET,"美股");
    }

    public static String getMarketDesc(Integer marketCode){
        return marketMap.get(marketCode);
    }

}
