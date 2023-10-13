package com.koder.stock.coreservice.manager.impl;

import com.koder.stock.client.dto.StockBasicDTO;
import com.koder.stock.coreservice.convertor.StockBasicConverter;
import com.koder.stock.coreservice.domain.dataobject.StockBasicDO;
import com.koder.stock.coreservice.domain.mapper.StockBasicDOMapper;
import com.koder.stock.coreservice.manager.StockBasicManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class StockBasicManagerImpl implements StockBasicManager {
    @Autowired
    private StockBasicDOMapper stockBasicDOMapper;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @Cacheable(cacheNames = "stockBasic_by_code")
    public StockBasicDTO queryByCode(String code) {
        StockBasicDO stockBasicDO = stockBasicDOMapper.queryByCode(code);
        return StockBasicConverter.fromDO(stockBasicDO);
    }
}
