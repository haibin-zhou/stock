package com.koder.stock.coreservice.scheduled;

import com.koder.stock.client.dto.StockBasicDTO;
import com.koder.stock.coreservice.config.GlobalConfig;
import com.koder.stock.coreservice.service.StockBasicInformationService;
import com.koder.stock.coreservice.util.DateUtil;
import com.koder.stock.coreservice.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class StockQuotationScheduled {
    @Autowired
    private GlobalConfig globalConfig;
    @Autowired
    private StockBasicInformationService stockBasicInformationService;
    @Autowired
    private SpringUtils springUtils;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Scheduled(cron = "0 15 06 ? * *")
    public void historyUSQuotationGettingTask() {
        if (!globalConfig.getQuotationHistory) {
            logger.warn("历史行情任务不启动，行情数据不获取，返回");
            return;
        }
        Long dateValue = Long.valueOf(DateUtil.getYYYYMMDDFromInstant(Instant.now(), DateUtil.DATE_FORMAT_YYYYMMDD));
        logger.warn("开始执行美股行情抓取任务，行情时间：{}",dateValue);
        List<StockBasicDTO> stockBasicList = stockBasicInformationService.queryAllStocks();
        stockBasicList.stream().filter(stockBasicDTO -> stockBasicDTO.getMarket().equals(20000))
                .forEach(stockBasicDTO -> {
            StockQuotationGettingTask task = springUtils.getBean(StockQuotationGettingTask.class);
            task.setStockCode(stockBasicDTO.getCode());
            task.setLimit(1);
            task.setGrabDate(dateValue);
            executorService.execute(task);
        });
    }

    @Scheduled(cron = "0 30 18 ? * *")
    public void historyHKQuotationGettingTask() {
        if (!globalConfig.getQuotationHistory) {
            logger.warn("历史行情任务不启动，行情数据不获取，返回");
            return;
        }
        Long dateValue = Long.valueOf(DateUtil.getYYYYMMDDFromInstant(Instant.now(), DateUtil.DATE_FORMAT_YYYYMMDD));

        logger.warn("开始执行港股行情抓取任务，行情时间{}",dateValue);
        List<StockBasicDTO> stockBasicList = stockBasicInformationService.queryAllStocks();

        stockBasicList.stream().filter(stockBasicDTO -> stockBasicDTO.getMarket().equals(10000))
                .forEach(stockBasicDTO -> {
            StockQuotationGettingTask task = springUtils.getBean(StockQuotationGettingTask.class);
            task.setStockCode(stockBasicDTO.getCode());
            task.setLimit(1);
            task.setGrabDate(dateValue);
            executorService.execute(task);
        });
    }

    public void historyQuotationGetting(String stockCode) {
        if (!globalConfig.getQuotationHistory) {
            logger.warn("历史行情任务不启动，行情数据不获取，返回");
            return;
        }
        StockQuotationGettingTask task = springUtils.getBean(StockQuotationGettingTask.class);
        task.setStockCode(stockCode);
        task.setLimit(500);
        task.setGrabDate(globalConfig.getQuotationStartDate());
        executorService.execute(task);
    }
}
