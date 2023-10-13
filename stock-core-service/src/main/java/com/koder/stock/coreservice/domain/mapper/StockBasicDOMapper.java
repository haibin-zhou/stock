package com.koder.stock.coreservice.domain.mapper;


import com.koder.stock.coreservice.domain.dataobject.StockBasicDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockBasicDOMapper {

    void insert(@Param("stock") StockBasicDO stockBasic);

    List<StockBasicDO> queryStocksByStatus(@Param("status") int status);

    StockBasicDO queryById(@Param("id") Long id);

    StockBasicDO queryByCode(@Param("code") String code);

    int updateFeatures(@Param("id") Long id, @Param("features")String features);

}