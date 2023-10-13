package com.koder.stock.coreservice.domain.mapper;

import com.koder.stock.coreservice.domain.dataobject.StockUserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StockUserDOMapper {

    StockUserDO queryByUP(@Param("mobilePhone") String mobilePhone, @Param("password") String password);

}
