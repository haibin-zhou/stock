package com.koder.stock.coreservice.config;

import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PriceGapConfig {
//    证券价格范围0.01-0.25 最小单位差价0.001
//    证券价格范围0.25-0.50 最小单位差价0.005
//    证券价格范围0.50-10.00 最小单位差价0.01
//    证券价格范围10.00-20.00 最小单位差价0.02
//    证券价格范围20.00-100.00 最小单位差价0.05
//    证券价格范围100.00-200.00 最小单位差价0.10
//    证券价格范围200.00-500.00 最小单位差价0.20
//    证券价格范围500.00-1000.00 最小单位差价0.50

    LEVEL_001("0.01", "0.25", "0.001"),
    LEVEL_025("0.25", "0.50", "0.005"),
    LEVEL_050("0.50", "10", "0.01"),
    LEVEL_10("10", "20", "0.02"),
    LEVEL_20("20", "100", "0.05"),
    LEVEL_100("100", "200", "0.1"),
    LEVEL_200("200", "500", "0.2"),
    LEVEL_500("500", "1000", "0.5"),
    ;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal priceGap;

    PriceGapConfig(String minPrice,String maxPrice,String priceGap){
        this.minPrice =  new BigDecimal(minPrice);
        this.maxPrice = new BigDecimal(maxPrice);
        this.priceGap = new BigDecimal(priceGap);
    }

    public static BigDecimal getPriceGap(BigDecimal entrustPrice) {
        PriceGapConfig[] priceGapConfigs = PriceGapConfig.values();
        List<PriceGapConfig> configList = Arrays.stream(priceGapConfigs).filter(priceGapConfig ->
                priceGapConfig.minPrice.compareTo(entrustPrice) <= 0 && priceGapConfig.maxPrice.compareTo(entrustPrice) > 0)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(configList)) {
            return null;
        }
        return configList.get(0).priceGap;
    }


}
