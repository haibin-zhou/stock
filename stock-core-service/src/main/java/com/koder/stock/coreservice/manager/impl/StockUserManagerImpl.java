package com.koder.stock.coreservice.manager.impl;

import com.koder.stock.client.dto.StockUserDTO;
import com.koder.stock.coreservice.convertor.StockUserConvertor;
import com.koder.stock.coreservice.domain.dataobject.StockUserDO;
import com.koder.stock.coreservice.domain.mapper.StockUserDOMapper;
import com.koder.stock.coreservice.manager.StockUserManager;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class StockUserManagerImpl implements StockUserManager {
    @Autowired
    private StockUserDOMapper stockUserDOMapper;

    @Override
    @Cacheable(cacheNames = "queryByUP")
    public StockUserDTO queryByUP(String mobilePhone, String password) {
        if (StringUtils.isBlank(mobilePhone) || StringUtils.isBlank(password)) {
            throw new RuntimeException("mobilePhone or password is required");
        }
        String entryPassword = DigestUtils.md5Hex(password);
        StockUserDO stockUserDO = stockUserDOMapper.queryByUP(mobilePhone, entryPassword);

        if (stockUserDO == null) {
            return null;
        }
        return StockUserConvertor.fromDO(stockUserDO);
    }

    public static void main(String args[]){
        String password = "zhb200666";
        System.out.println(DigestUtils.md5Hex(password));
    }
}
