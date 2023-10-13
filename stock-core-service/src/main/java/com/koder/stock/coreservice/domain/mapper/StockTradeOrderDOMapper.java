package com.koder.stock.coreservice.domain.mapper;

import com.koder.stock.coreservice.domain.dataobject.StockTradeOrderDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockTradeOrderDOMapper {



    void batchAdd(@Param("tradeOrders") List<StockTradeOrderDO> stockTradeOrders);

    List<StockTradeOrderDO> queryUserOrdersByStatus(@Param("userId") Long userId,
                                                    @Param("stockCode") String stockCode, @Param("statusList") List<String> statusList);

    StockTradeOrderDO queryOrderByTradeNo(@Param("tradeOrderNo") String tradeOrderNo);

    StockTradeOrderDO queryOrderById(@Param("id") Long id);

    StockTradeOrderDO queryOrderByOuterTradeNo(@Param("outTradeOrderNo") String outTradeOrderNo);

    void updateEntrustStatus(@Param("orderId") Long orderId, @Param("status")String status, @Param("outerTradeNo")String outerTradeNo,@Param("features") String features);

    void updateEntrustCallback(@Param("stockTradeOrderDO")StockTradeOrderDO stockTradeOrderDO);

    List<StockTradeOrderDO> queryReCreateOrder(@Param("market") String market);

    void updateReCreatedStatus(@Param("idList")List<Long> idList, @Param("fromStatus")String fromStatus, @Param("toStatus")String toStatus);

}
