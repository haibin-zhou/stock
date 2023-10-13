package com.koder.stock.coreservice.service.impl;

import com.koder.stock.client.dto.StockQuotationDTO;
import com.koder.stock.coreservice.convertor.StockQuotationConvertor;
import com.koder.stock.coreservice.domain.constant.StockBasicFeatureKey;
import com.koder.stock.coreservice.domain.dataobject.StockQuotationDO;
import com.koder.stock.coreservice.domain.mapper.StockQuotationDOMapper;
import com.koder.stock.coreservice.service.StockBasicInformationService;
import com.koder.stock.coreservice.service.StockQuotationService;
import com.koder.stock.coreservice.util.CollectionUtil;
import com.koder.stock.coreservice.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StockQuotationServiceImpl implements StockQuotationService {

    @Autowired
    private StockQuotationDOMapper stockQuotationDOMapper;
    @Autowired
    private StockBasicInformationService stockBasicInformationService;

    @Override
    @Transactional
    public void batchAdd(Long stockId, List<StockQuotationDTO> stockQuotationDTOs) {
//        if (CollectionUtils.isEmpty(stockQuotationDTOs)) {
//            stockBasicInformationService.updateFeatures(stockId, StockBasicFeatureKey.QUOTATION_DATE, DateUtil.getYYYYMMDDFromInstant(end, DateUtil.DATE_FORMAT_YYYY_MM_DD));
//            return;
//        }
        if (CollectionUtils.isEmpty(stockQuotationDTOs)) {
            log.error("batchAdd stock quotation return, List is empty");
            return;
        }
        List<StockQuotationDO> converts = stockQuotationDTOs.stream()
                .map(stockQuotationDTO -> {
                    stockQuotationDTO.setStockId(stockId);
                    return StockQuotationConvertor.fromDTO(stockQuotationDTO);
                }).collect(Collectors.toList());

        List<List<StockQuotationDO>> batches = CollectionUtil.splitCollections(converts, 1000);
        for (List<StockQuotationDO> batch : batches) {
            try {
                stockQuotationDOMapper.batchInsert(batch);
            } catch (Exception e) {
                log.error("batchAdd stock quotation error", e);
            }
        }
//        stockBasicInformationService.updateFeatures(stockId, StockBasicFeatureKey.QUOTATION_DATE, DateUtil.getYYYYMMDDFromInstant(end, DateUtil.DATE_FORMAT_YYYY_MM_DD));
    }
}
