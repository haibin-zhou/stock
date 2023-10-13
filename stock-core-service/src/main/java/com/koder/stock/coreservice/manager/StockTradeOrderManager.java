package com.koder.stock.coreservice.manager;

import com.koder.stock.client.dto.StockTradeOrderDTO;

import java.util.List;

public interface StockTradeOrderManager {

    List<StockTradeOrderDTO> batchAdd(List<StockTradeOrderDTO> stockTradeOrders);

    List<StockTradeOrderDTO> queryUnfinishedTradeOrders(Long userId,String stockCode);

    StockTradeOrderDTO queryOrderByTradeNo(String tradeNo);

    StockTradeOrderDTO queryOrderByOuterTradeNo(String outerTradeNo);

    void updateEntrustStatus(StockTradeOrderDTO stockTradeOrderDTO);

    void updateEntrustCallBack(StockTradeOrderDTO stockTradeOrderDTO);

    List<StockTradeOrderDTO> queryRecreateOrders(String market);

    void updateReCreatedStatus(List<Long> orderIdList,String fromStatus,String toStatus);
}
