package com.koder.stock.coreservice.scheduled;

import com.koder.stock.client.dto.StockUserFundDTO;
import com.koder.stock.coreservice.config.GlobalConfig;
import com.koder.stock.coreservice.domain.constant.StockExchangeTypeConstant;
import com.koder.stock.coreservice.manager.StockUserFundManager;
import com.koder.stock.coreservice.thirdparty.hs.HsApiProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class StockUserFundScheduled {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private HsApiProxy hsApiProxy;
    @Autowired
    private GlobalConfig globalConfig;
    @Autowired
    private StockUserFundManager stockUserFundManager;

//    @Scheduled(cron = "0 30 18 ? * *")
    private void syncHKFund() {
        String exchangeType = StockExchangeTypeConstant.HK_EXCHANGE_TYPE;
        StockUserFundDTO stockUserFundDTO = queryUserFund(exchangeType);
        stockUserFundManager.add(stockUserFundDTO);
    }

//    @Scheduled(cron = "0 15 09 ? * *")
    private void syncUSFund() {
        String exchangeType = StockExchangeTypeConstant.US_EXCHANGE_TYPE;
        StockUserFundDTO stockUserFundDTO = queryUserFund(exchangeType);
        stockUserFundManager.add(stockUserFundDTO);
    }

    public StockUserFundDTO queryUserFund(String exchangeType) {
        StockUserFundDTO stockUserFundDTO = hsApiProxy.getUserFund(exchangeType);
        stockUserFundDTO.setCreateTime(Instant.now().toEpochMilli());
        stockUserFundDTO.setModifiedTime(Instant.now().toEpochMilli());
        return stockUserFundDTO;
    }

//    @Scheduled(cron = "0 */5 * * * ?")
    public void executeAutoLogin() {
        logger.info("execute auto login begin");
        StockExchangeTypeConstant.exchangeTypeMap.forEach((k, v) -> {
            queryUserFund(v);
        });
        logger.info("execute auto login end");
    }
}
