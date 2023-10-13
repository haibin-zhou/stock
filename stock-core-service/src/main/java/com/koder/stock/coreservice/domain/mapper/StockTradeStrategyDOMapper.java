package com.koder.stock.coreservice.domain.mapper;

import com.koder.stock.coreservice.domain.dataobject.StockTradeStrategyDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockTradeStrategyDOMapper {

    List<StockTradeStrategyDO> queryStrategyByStockId(@Param("stockId") Long stockId);

    StockTradeStrategyDO queryStrategyById(@Param("id")Long id);

}
