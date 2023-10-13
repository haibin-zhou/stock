package com.koder.stock.coreservice.manager;

import com.koder.stock.client.dto.StockTradeStrategyDTO;

import java.util.List;

public interface StockTradeStrategyManager {

    List<StockTradeStrategyDTO> queryStrategyByStockId(Long stockId);

    StockTradeStrategyDTO queryStrategyById(Long id);

}
