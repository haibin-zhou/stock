package com.koder.stock.coreservice.domain.mapper;

import com.koder.stock.coreservice.domain.dataobject.StockUserFundDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StockUserFundDOMapper {
    void add(@Param("fund") StockUserFundDO stockUserFundDO);
}
