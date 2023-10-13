package com.koder.stock.coreservice.manager;

import com.koder.stock.client.dto.StockUserDTO;

public interface StockUserManager {

    StockUserDTO queryByUP(String mobilePhone,String password);

}
