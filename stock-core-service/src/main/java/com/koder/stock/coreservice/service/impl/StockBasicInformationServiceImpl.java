package com.koder.stock.coreservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.koder.stock.client.dto.StockBasicDTO;
import com.koder.stock.coreservice.convertor.StockBasicConverter;
import com.koder.stock.coreservice.domain.constant.StockBasicStatus;
import com.koder.stock.coreservice.domain.dataobject.StockBasicDO;
import com.koder.stock.coreservice.domain.mapper.StockBasicDOMapper;
import com.koder.stock.coreservice.service.StockBasicInformationService;
import com.koder.stock.coreservice.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StockBasicInformationServiceImpl implements StockBasicInformationService {

    @Autowired
    private StockBasicDOMapper stockBasicDOMapper;

    @Override
    public StockBasicDTO add(StockBasicDTO stockBasicDTO) {
        StockBasicDO tempDO = StockBasicConverter.fromDTO(stockBasicDTO);
        tempDO.setModifiedTime(Instant.now().toEpochMilli());
        tempDO.setCreateTime(Instant.now().toEpochMilli());
        try {
            stockBasicDOMapper.insert(tempDO);
        } catch (Exception e) {
            log.error("insert data error", e);
        }
        return stockBasicDTO;
    }

    @Override
    public List<StockBasicDTO> queryAllStocks() {
        List<StockBasicDO> basics = stockBasicDOMapper.queryStocksByStatus(StockBasicStatus.NORMAL);
        return basics.stream().map(basicDO -> StockBasicConverter.fromDO(basicDO)).collect(Collectors.toList());
    }

    @Override
    public StockBasicDTO queryByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new IllegalArgumentException("code required");
        }
        StockBasicDO stockBasicDO = stockBasicDOMapper.queryByCode(code);
        if (stockBasicDO == null) {
            throw new IllegalArgumentException("code incorrectly");
        }
        return StockBasicConverter.fromDO(stockBasicDO);
    }

    @Override
    public void updateFeatures(Long stockId, String key, String value) {
        StockBasicDO stockBasicDO = stockBasicDOMapper.queryById(stockId);
        if (stockBasicDO == null) {
            return;
        }
        Map<String, String> featureMap = CollectionUtil.convertFeaturesMap(stockBasicDO.getFeatures());
        featureMap.put(key, value);
        String features = JSON.toJSONString(featureMap);
        stockBasicDOMapper.updateFeatures(stockId, features);
    }

    @Override
    public void updateStatus(Long stockId, Integer fromStatus, Integer toStatus) {


    }
}
