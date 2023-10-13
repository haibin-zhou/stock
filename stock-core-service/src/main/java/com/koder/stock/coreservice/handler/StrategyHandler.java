package com.koder.stock.coreservice.handler;

import com.koder.stock.client.dto.StockQuotationDTO;
import com.koder.stock.client.dto.StockTradeOrderDTO;
import com.koder.stock.client.dto.StockTradeStrategyDTO;

import java.util.List;

public interface StrategyHandler {

    String getCode();

    List<StockTradeOrderDTO> buildTradeOrder(StockTradeStrategyDTO stockTradeStrategyDTO, StockQuotationDTO stockQuotationDTO);

}
