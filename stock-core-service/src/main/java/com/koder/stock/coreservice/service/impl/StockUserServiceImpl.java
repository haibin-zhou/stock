package com.koder.stock.coreservice.service.impl;

import com.koder.stock.client.dto.ResultDTO;
import com.koder.stock.client.dto.StockUserDTO;
import com.koder.stock.coreservice.manager.StockUserManager;
import com.koder.stock.coreservice.service.StockUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockUserServiceImpl implements StockUserService {
    @Autowired
    private StockUserManager stockUserManager;
    @Override
    public ResultDTO<StockUserDTO> queryByUP(String mobilePhone, String password) {
        if (StringUtils.isBlank(mobilePhone) || StringUtils.isBlank(password)) {
            throw new RuntimeException("mobilePhone or password is required");
        }
        StockUserDTO stockUserDTO = stockUserManager.queryByUP(mobilePhone,password);
        return ResultDTO.of(stockUserDTO);
    }
}
