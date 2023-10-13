package com.koder.stock.coreservice.convertor;

import com.koder.stock.client.dto.StockUserFundDTO;
import com.koder.stock.coreservice.domain.dataobject.StockUserFundDO;

public class StockUserFundConvertor {

    public static StockUserFundDO fromDTO(StockUserFundDTO stockUserFUndDTO) {
        StockUserFundDO stockUserFundDO = StockUserFundDO.builder()
                .userId(stockUserFUndDTO.getUserId())
                .assetBalance(stockUserFUndDTO.getAssetBalance())
                .availableBalance(stockUserFUndDTO.getAvailableBalance())
                .createTime(stockUserFUndDTO.getCreateTime())
                .modifiedTime(stockUserFUndDTO.getModifiedTime())
                .holdingBalance(stockUserFUndDTO.getHoldingBalance())
                .exchangeType(stockUserFUndDTO.getExchangeType())
                .assetDate(stockUserFUndDTO.getAssetDate())
                .build();
        return stockUserFundDO;
    }

    public static StockUserFundDTO fromDO(StockUserFundDO stockUserFUndDO) {
        StockUserFundDTO stockUserFundDTO = StockUserFundDTO.builder()
                .userId(stockUserFUndDO.getUserId())
                .assetBalance(stockUserFUndDO.getAssetBalance())
                .availableBalance(stockUserFUndDO.getAvailableBalance())
                .createTime(stockUserFUndDO.getCreateTime())
                .modifiedTime(stockUserFUndDO.getModifiedTime())
                .assetDate(stockUserFUndDO.getAssetDate())
                .userId(stockUserFUndDO.getUserId())
                .holdingBalance(stockUserFUndDO.getHoldingBalance())
                .exchangeType(stockUserFUndDO.getExchangeType())
                .build();
        return stockUserFundDTO;
    }


}
