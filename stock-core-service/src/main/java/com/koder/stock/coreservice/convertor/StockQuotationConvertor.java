package com.koder.stock.coreservice.convertor;

import com.alibaba.fastjson.JSON;
import com.koder.stock.client.dto.StockQuotationDTO;
import com.koder.stock.coreservice.domain.dataobject.StockQuotationDO;

import java.time.Instant;

public class StockQuotationConvertor {

        public static StockQuotationDO fromDTO(StockQuotationDTO stockQuotationDTO){
            StockQuotationDO quotationDO = StockQuotationDO.builder()
                    .quotationTime(stockQuotationDTO.getQuotationTime().toEpochMilli())
                    .changeAmount(stockQuotationDTO.getChangeAmount())
                    .features(JSON.toJSONString(stockQuotationDTO.getFeatures()))
                    .changeRange(stockQuotationDTO.getChangeRange())
                    .stockId(stockQuotationDTO.getStockId())
                    .closingPrice(stockQuotationDTO.getClosingPrice())
                    .highestPrice(stockQuotationDTO.getHighestPrice())
                    .lastClosingPrice(stockQuotationDTO.getLastClosingPrice())
                    .lowestPrice(stockQuotationDTO.getLowestPrice())
                    .openingPrice(stockQuotationDTO.getOpeningPrice())
                    .turnOverAmount(stockQuotationDTO.getTurnOverAmount())
                    .turnOverRate(stockQuotationDTO.getTurnOverRate())
                    .turnOverVolume(stockQuotationDTO.getTurnOverVolume())
                    .code(stockQuotationDTO.getCode())
                    .build();
            quotationDO.setCreateTime(Instant.now().toEpochMilli());
            quotationDO.setModifiedTime(Instant.now().toEpochMilli());
            return quotationDO;
        }





}
