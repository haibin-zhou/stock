package com.koder.stock.coreservice.convertor;

import com.alibaba.fastjson.JSON;
import com.koder.stock.client.dto.StockTradeOrderDTO;
import com.koder.stock.coreservice.domain.dataobject.StockTradeOrderDO;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StockTradeOrderConvertor {

    public static StockTradeOrderDO fromDTO(StockTradeOrderDTO stockTradeOrderDTO) {
        StockTradeOrderDO stockTradeOrderDO = StockTradeOrderDO.builder()
                .tradeOrderNo(stockTradeOrderDTO.getTradeOrderNo())
                .tradeWhenClose(stockTradeOrderDTO.getTradeWhenClose())
                .stockCode(stockTradeOrderDTO.getStockCode())
                .features(JSON.toJSONString(stockTradeOrderDTO.getFeatures()))
                .createTime(stockTradeOrderDTO.getCreateTime() == null ? Calendar.getInstance().getTimeInMillis() : stockTradeOrderDTO.getCreateTime())
                .modifiedTime(stockTradeOrderDTO.getModifiedTime() == null ? Calendar.getInstance().getTimeInMillis() : stockTradeOrderDTO.getModifiedTime())
                .entrustBs(stockTradeOrderDTO.getEntrustBs())
                .entrustPrice(stockTradeOrderDTO.getEntrustPrice())
                .entrustCount(stockTradeOrderDTO.getEntrustVolume())
                .status(stockTradeOrderDTO.getStatus())
                .entrustType(stockTradeOrderDTO.getEntrustType())
                .strategyType(stockTradeOrderDTO.getStrategyType())
                .iceBergDisplaySize(stockTradeOrderDTO.getIceBergDisplaySize())
                .id(stockTradeOrderDTO.getId())
                .status(stockTradeOrderDTO.getStatus())
                .userId(stockTradeOrderDTO.getUserId())
                .exchange(stockTradeOrderDTO.getExchange())
                .exchangeType(stockTradeOrderDTO.getExchangeType())
                .dealDateTime(stockTradeOrderDTO.getDealDateTime())
                .dealCount(stockTradeOrderDTO.getDealCount())
                .dealPrice(stockTradeOrderDTO.getDealPrice())
                .strategyId(stockTradeOrderDTO.getStrategyId())
                .reCreate(stockTradeOrderDTO.getReCreate())
                .build();
        return stockTradeOrderDO;
    }

    public static StockTradeOrderDTO fromDO(StockTradeOrderDO stockTradeOrderDO) {
        StockTradeOrderDTO stockTradeOrderDTO = StockTradeOrderDTO.builder()
                .tradeOrderNo(stockTradeOrderDO.getTradeOrderNo())
                .tradeWhenClose(stockTradeOrderDO.getTradeWhenClose())
                .stockCode(stockTradeOrderDO.getStockCode())
                .createTime(stockTradeOrderDO.getCreateTime() == null ? Calendar.getInstance().getTimeInMillis() : stockTradeOrderDO.getCreateTime())
                .modifiedTime(stockTradeOrderDO.getModifiedTime() == null ? Calendar.getInstance().getTimeInMillis() : stockTradeOrderDO.getModifiedTime())
                .entrustBs(stockTradeOrderDO.getEntrustBs())
                .entrustPrice(stockTradeOrderDO.getEntrustPrice())
                .entrustVolume(stockTradeOrderDO.getEntrustCount())
                .entrustType(stockTradeOrderDO.getEntrustType())
                .iceBergDisplaySize(stockTradeOrderDO.getIceBergDisplaySize())
                .status(stockTradeOrderDO.getStatus())
                .dealDateTime(stockTradeOrderDO.getDealDateTime())
                .dealCount(stockTradeOrderDO.getDealCount())
                .dealPrice(stockTradeOrderDO.getDealPrice())
                .userId(stockTradeOrderDO.getUserId())
                .exchange(stockTradeOrderDO.getExchange())
                .strategyId(stockTradeOrderDO.getStrategyId())
                .exchangeType(stockTradeOrderDO.getExchangeType())
                .id(stockTradeOrderDO.getId())
                .reCreate(stockTradeOrderDO.getReCreate())
                .build();

        Map<String,String> featuresMap = JSON.parseObject(stockTradeOrderDO.getFeatures(),HashMap.class);
        stockTradeOrderDTO.addFeatures(featuresMap);
        return stockTradeOrderDTO;
    }

}
