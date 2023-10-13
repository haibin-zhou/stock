package com.koder.stock.coreservice.manager;

import com.koder.stock.client.dto.StockBasicDTO;

public interface StockBasicManager {

    StockBasicDTO queryByCode(String code);



}
