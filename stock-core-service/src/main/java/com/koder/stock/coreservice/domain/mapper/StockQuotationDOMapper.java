package com.koder.stock.coreservice.domain.mapper;

import com.koder.stock.coreservice.domain.dataobject.StockQuotationDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockQuotationDOMapper {

    void batchInsert(@Param("quotations") List<StockQuotationDO> quotations);

}

