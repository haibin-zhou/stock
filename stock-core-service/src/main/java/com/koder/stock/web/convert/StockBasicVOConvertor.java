package com.koder.stock.web.convert;

import com.koder.stock.client.dto.StockBasicDTO;
import com.koder.stock.client.vo.StockBasicVO;
import com.koder.stock.coreservice.domain.constant.StockBasicFeatureKey;
import com.koder.stock.coreservice.domain.constant.StockBasicMarket;
import com.koder.stock.coreservice.domain.constant.StockBasicStatus;
import com.koder.stock.coreservice.util.DateUtil;

import java.time.Instant;

public class StockBasicVOConvertor {

    public static StockBasicVO fromDTO(StockBasicDTO stockBasicDTO) {
        StockBasicVO stockBasicVO = StockBasicVO.builder()
                .code(stockBasicDTO.getCode())
                .id(stockBasicDTO.getId())
                .modifiedTime(DateUtil.getYYYYMMDDFromInstant(Instant.ofEpochMilli(stockBasicDTO.getModifiedTime()), DateUtil.DATE_FORMAT_YYYY_MM_DD))
                .market(StockBasicMarket.getMarketDesc(stockBasicDTO.getMarket()))
                .name(stockBasicDTO.getName())
                .status(StockBasicStatus.getStockBasicStatusDesc(stockBasicDTO.getStatus()))
                .quotationTime(stockBasicDTO.getFeatures().get(StockBasicFeatureKey.QUOTATION_DATE))
                .build();
        return stockBasicVO;
    }


}
