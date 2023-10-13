package com.koder.stock.coreservice.service;

import com.koder.stock.client.dto.StockQuotationDTO;

import java.time.Instant;
import java.util.List;

public interface StockQuotationService {

    void batchAdd(Long stockId,List<StockQuotationDTO> stockQuotationDTOs);

}
