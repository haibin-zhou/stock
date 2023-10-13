package com.koder.stock.coreservice.manager.impl;

import com.koder.stock.client.dto.StockTradeStrategyDTO;
import com.koder.stock.coreservice.convertor.StockTradeStrategyConvertor;
import com.koder.stock.coreservice.domain.dataobject.StockTradeStrategyDO;
import com.koder.stock.coreservice.domain.mapper.StockTradeStrategyDOMapper;
import com.koder.stock.coreservice.manager.StockTradeStrategyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StockTradeStrategyManagerImpl implements StockTradeStrategyManager {
    @Autowired
    private StockTradeStrategyDOMapper stockTradeStrategyDOMapper;

    @Cacheable(cacheNames = "queryStrategyByStockId")
    public List<StockTradeStrategyDTO> queryStrategyByStockId(Long stockId) {
        List<StockTradeStrategyDO> strategyDOs = stockTradeStrategyDOMapper.queryStrategyByStockId(stockId);
        return strategyDOs.stream()
                .map(stockTradeStrategyDO -> StockTradeStrategyConvertor.fromDO(stockTradeStrategyDO))
                .collect(Collectors.toList());
    }
    @Cacheable(cacheNames = "queryStrategyById")
    @Override
    public StockTradeStrategyDTO queryStrategyById(Long id) {
        StockTradeStrategyDO stockTradeStrategyDO = stockTradeStrategyDOMapper.queryStrategyById(id);
        return StockTradeStrategyConvertor.fromDO(stockTradeStrategyDO);
    }


}
