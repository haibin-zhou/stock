package com.koder.stock.coreservice.service;

import com.koder.stock.client.dto.StockBasicDTO;

import java.util.List;

public interface StockBasicInformationService {

    StockBasicDTO add(StockBasicDTO stockBasicDTO);

    List<StockBasicDTO> queryAllStocks();

    StockBasicDTO queryByCode(String code);

    void updateFeatures(Long stockId, String key, String value);

    void updateStatus(Long stockId,Integer fromStatus,Integer toStatus);

}
