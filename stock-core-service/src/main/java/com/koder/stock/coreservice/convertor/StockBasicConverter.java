package com.koder.stock.coreservice.convertor;

import com.alibaba.fastjson.JSON;
import com.koder.stock.client.dto.StockBasicDTO;
import com.koder.stock.coreservice.domain.dataobject.StockBasicDO;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class StockBasicConverter {

    public static StockBasicDO fromDTO(StockBasicDTO stockBasicDTO) {
        return StockBasicDO.builder()
                .market(stockBasicDTO.getMarket())
                .code(stockBasicDTO.getCode())
                .name(stockBasicDTO.getName())
                .exchange(stockBasicDTO.getExchange())
                .status(stockBasicDTO.getStatus() == null ? 1 : stockBasicDTO.getStatus())
                .features(JSON.toJSONString(stockBasicDTO.getFeatures()))
                .build();
    }

    public static StockBasicDTO fromDO(StockBasicDO stockBasicDO) {

        StockBasicDTO stockBasicDTO = StockBasicDTO.builder()
                .market(stockBasicDO.getMarket())
                .code(stockBasicDO.getCode())
                .name(stockBasicDO.getName())
                .id(stockBasicDO.getId())
                .createTime(stockBasicDO.getCreateTime())
                .modifiedTime(stockBasicDO.getModifiedTime())
                .exchange(stockBasicDO.getExchange())
                .status(stockBasicDO.getStatus())
                .build();

        if (!StringUtils.isEmpty(stockBasicDO.getFeatures())) {
            Map<String, String> features = JSON.parseObject(stockBasicDO.getFeatures(), HashMap.class);
            stockBasicDTO.addFeatures(features);
        }
        return stockBasicDTO;
    }


}
