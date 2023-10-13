package com.koder.stock.coreservice.manager.impl;

import com.koder.stock.client.dto.StockUserFundDTO;
import com.koder.stock.coreservice.config.GlobalConfig;
import com.koder.stock.coreservice.convertor.StockUserFundConvertor;
import com.koder.stock.coreservice.domain.dataobject.StockUserFundDO;
import com.koder.stock.coreservice.domain.mapper.StockUserFundDOMapper;
import com.koder.stock.coreservice.manager.StockUserFundManager;
import com.koder.stock.coreservice.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class StockUserFundManagerImpl implements StockUserFundManager {
    @Autowired
    private StockUserFundDOMapper stockUserFundDOMapper;
    @Autowired
    private GlobalConfig globalConfig;

    @Override
    public StockUserFundDTO add(StockUserFundDTO stockUserFundDTO) {
        if (stockUserFundDTO == null) {
            return null;
        }
        stockUserFundDTO.setCreateTime(Instant.now().toEpochMilli());
        stockUserFundDTO.setModifiedTime(Instant.now().toEpochMilli());
        stockUserFundDTO.setUserId(globalConfig.getSystemDefaultUserId());
        stockUserFundDTO.setAssetDate(DateUtil.getStartTimeOfDay(Instant.now().toEpochMilli()));
        StockUserFundDO stockUserFundDO = StockUserFundConvertor.fromDTO(stockUserFundDTO);
        stockUserFundDOMapper.add(stockUserFundDO);
        return StockUserFundConvertor.fromDO(stockUserFundDO);
    }
}
