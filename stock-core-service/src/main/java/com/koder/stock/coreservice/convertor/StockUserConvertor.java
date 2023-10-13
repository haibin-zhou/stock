package com.koder.stock.coreservice.convertor;

import com.alibaba.fastjson.JSON;
import com.koder.stock.client.dto.StockUserDTO;
import com.koder.stock.coreservice.domain.dataobject.StockUserDO;

import java.time.Instant;
import java.util.HashMap;

public class StockUserConvertor {

    public static StockUserDTO fromDO(StockUserDO stockUserDO) {
        StockUserDTO stockUserDTO = StockUserDTO.builder()
                .modifiedTime(Instant.ofEpochMilli(stockUserDO.getModifiedTime()))
                .createTime(Instant.ofEpochMilli(stockUserDO.getCreateTime()))
                .features(JSON.parseObject(stockUserDO.getFeatures(), HashMap.class))
                .id(stockUserDO.getId())
                .loginPwd(stockUserDO.getLoginPwd())
                .mobilePhone(stockUserDO.getMobilePhone())
                .fundAccount(stockUserDO.getFundAccount())
                .tradePwd(stockUserDO.getTradePwd())
                .build();
        return stockUserDTO;
    }


}
