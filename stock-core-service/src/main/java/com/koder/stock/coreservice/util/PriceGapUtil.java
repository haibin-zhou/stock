package com.koder.stock.coreservice.util;

import com.koder.stock.coreservice.config.PriceGapConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceGapUtil {

    public final static String hkExchangeType = "K";

    public static BigDecimal calculatePriceGap(String exchangeType, BigDecimal expectedPrice) {
        // 获取价差
        if (!exchangeType.equals(hkExchangeType)) {
            return expectedPrice;
        }
        BigDecimal priceGap = PriceGapConfig.getPriceGap(expectedPrice);
        BigDecimal divideResult = expectedPrice.divide(priceGap);
        divideResult = divideResult.setScale(0, RoundingMode.UP);
        BigDecimal result = divideResult.multiply(priceGap);
        return result;
    }

    public static void main(String args[]){
        BigDecimal result = PriceGapUtil.calculatePriceGap("K",new BigDecimal("308.5"));
        System.out.println(result.toPlainString());
    }




}
