package com.koder.stock.coreservice.service;

import com.koder.stock.client.dto.ResultDTO;
import com.koder.stock.client.dto.StockUserDTO;

public interface StockUserService {

    ResultDTO<StockUserDTO> queryByUP(String mobilePhone, String password);

}
