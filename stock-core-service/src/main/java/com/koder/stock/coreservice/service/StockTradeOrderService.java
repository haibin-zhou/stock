package com.koder.stock.coreservice.service;

import com.koder.stock.client.dto.ResultDTO;
import com.koder.stock.client.dto.StockQuotationDTO;
import com.koder.stock.client.dto.StockTradeOrderDTO;

import java.util.List;
/**
 * 创建交易订单服务
 */
public interface StockTradeOrderService {

    List<StockTradeOrderDTO> createTradeOrders(StockQuotationDTO stockQuotationDTO);
    /**
     * @param stockTradeOrderDTO
     * @return
     */
    void entrustCallback(StockTradeOrderDTO stockTradeOrderDTO);

    ResultDTO<StockTradeOrderDTO> createTradeOrder(StockTradeOrderDTO stockTradeOrderDTO);

    void entrustStatusUpdate(StockTradeOrderDTO stockTradeOrderDTO);

    List<StockTradeOrderDTO> queryUnEntrustedOrders(Long userId,String stockCode,String entrustBs);
    
    void reCreateOrder(String market);
}
