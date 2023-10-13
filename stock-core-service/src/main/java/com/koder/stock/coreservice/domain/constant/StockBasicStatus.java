package com.koder.stock.coreservice.domain.constant;

import java.util.HashMap;
import java.util.Map;

public class StockBasicStatus {
    public static int IN_LIST = 0;
    public static int NORMAL = 1;
    public static int STOP_TRANSACTION = 2;

    private static Map<Integer,String> stockBasicStatusMap = new HashMap<>();

    static {
        stockBasicStatusMap.put(IN_LIST,"观察");
        stockBasicStatusMap.put(NORMAL,"交易");
        stockBasicStatusMap.put(STOP_TRANSACTION,"交易停止");
    }

    public static String getStockBasicStatusDesc(Integer status){
        return stockBasicStatusMap.get(status);
    }






}
