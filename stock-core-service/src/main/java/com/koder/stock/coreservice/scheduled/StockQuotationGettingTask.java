package com.koder.stock.coreservice.scheduled;

import com.koder.stock.client.dto.StockBasicDTO;
import com.koder.stock.client.dto.StockQuotationDTO;
import com.koder.stock.coreservice.config.GlobalConfig;
import com.koder.stock.coreservice.service.StockBasicInformationService;
import com.koder.stock.coreservice.service.StockQuotationService;
import com.koder.stock.coreservice.thirdparty.hs.HsApiProxy;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
@Data
public class StockQuotationGettingTask implements Runnable {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String stockCode;
    private Integer limit = 1;
    private Long grabDate;
    @Autowired
    private HsApiProxy hsApiProxy;
    @Autowired
    private StockBasicInformationService stockBasicInformationService;

    @Autowired
    private StockQuotationService stockQuotationService;

    @Override
    public void run() {
        if (this.stockCode == null) {
            logger.warn("股票代码Code没设置，创建之后请设置具体的股票代码");
            return;
        }
        StockBasicDTO stockBasicDTO = stockBasicInformationService.queryByCode(this.stockCode);
        if (stockBasicDTO == null) {
            logger.warn("股票代码[{}]不存在，退出", this.stockCode);
            return;
        }
        List<StockQuotationDTO> quotations = hsApiProxy.queryQuotation(stockBasicDTO.getMarket(),stockCode, grabDate, 2, this.limit);
        this.stockQuotationService.batchAdd(stockBasicDTO.getId(), quotations);
    }

}
