package com.koder.stock.coreservice.scheduled;

import com.koder.stock.coreservice.domain.constant.StockExchangeTypeConstant;
import com.koder.stock.coreservice.service.StockTradeOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StockTradeOrderReCreateScheduled {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StockTradeOrderService stockTradeOrderService;

    @Scheduled(cron = "0 05 10 ? * MON-FRI")
    public void tradeOrderRecreateForHK(){
        stockTradeOrderService.reCreateOrder(StockExchangeTypeConstant.HK_EXCHANGE_TYPE);
    }

    @Scheduled(cron = "0 35 21 ? * MON-FRI")
    public void tradeOrderRecreateForUS(){
        stockTradeOrderService.reCreateOrder(StockExchangeTypeConstant.US_EXCHANGE_TYPE);
    }
}
