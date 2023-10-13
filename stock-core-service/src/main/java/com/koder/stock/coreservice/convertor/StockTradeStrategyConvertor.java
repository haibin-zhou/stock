package com.koder.stock.coreservice.convertor;

import com.alibaba.fastjson.JSON;
import com.koder.stock.client.dto.StockTradeStrategyDTO;
import com.koder.stock.coreservice.domain.dataobject.StockTradeStrategyDO;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.HashMap;

public class StockTradeStrategyConvertor {

    public static StockTradeStrategyDO fromDTO(StockTradeStrategyDTO stockTradeStrategyDTO) {
        StockTradeStrategyDO stockTradeStrategyDO = StockTradeStrategyDO.builder()
                .createTime(stockTradeStrategyDTO.getCreateTime() == null ? Calendar.getInstance().getTimeInMillis() : stockTradeStrategyDTO.getCreateTime())
                .modifiedTime(stockTradeStrategyDTO.getModifiedTime() == null ? Calendar.getInstance().getTimeInMillis() : stockTradeStrategyDTO.getModifiedTime())
                .endTime(stockTradeStrategyDTO.getEndTime())
                .startTime(stockTradeStrategyDTO.getStartTime())
                .stockId(stockTradeStrategyDTO.getStockId())
                .strategyType(stockTradeStrategyDTO.getStrategyType())
                .strategyContent(stockTradeStrategyDTO.getStrategyContent())
                .build();
        stockTradeStrategyDO.setFeatures(JSON.toJSONString(stockTradeStrategyDTO.getFeatures()));
        return stockTradeStrategyDO;
    }

    public static StockTradeStrategyDTO fromDO(StockTradeStrategyDO stockTradeStrategyDO) {
        StockTradeStrategyDTO stockTradeStrategyDTO = StockTradeStrategyDTO.builder()
                .createTime(stockTradeStrategyDO.getCreateTime() == null ? Calendar.getInstance().getTimeInMillis() : stockTradeStrategyDO.getCreateTime())
                .modifiedTime(stockTradeStrategyDO.getModifiedTime() == null ? Calendar.getInstance().getTimeInMillis() : stockTradeStrategyDO.getModifiedTime())
                .endTime(stockTradeStrategyDO.getEndTime())
                .startTime(stockTradeStrategyDO.getStartTime())
                .id(stockTradeStrategyDO.getId())
                .userId(stockTradeStrategyDO.getUserId())
                .status(stockTradeStrategyDO.getStatus())
                .stockId(stockTradeStrategyDO.getStockId())
                .strategyType(stockTradeStrategyDO.getStrategyType())
                .strategyContent(stockTradeStrategyDO.getStrategyContent())
                .build();
        if (StringUtils.isNotBlank(stockTradeStrategyDO.getFeatures())) {
            stockTradeStrategyDTO.addFeatures(JSON.parseObject(stockTradeStrategyDO.getFeatures(), HashMap.class));
        }
        return stockTradeStrategyDTO;
    }

}
