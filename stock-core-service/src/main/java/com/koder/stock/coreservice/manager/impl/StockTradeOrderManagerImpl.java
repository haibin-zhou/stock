package com.koder.stock.coreservice.manager.impl;

import com.alibaba.fastjson.JSON;
import com.koder.stock.client.dto.StockTradeOrderDTO;
import com.koder.stock.coreservice.config.GlobalConfig;
import com.koder.stock.coreservice.convertor.StockTradeOrderConvertor;
import com.koder.stock.coreservice.domain.constant.StockEntrustStatus;
import com.koder.stock.coreservice.domain.dataobject.StockTradeOrderDO;
import com.koder.stock.coreservice.domain.mapper.StockTradeOrderDOMapper;
import com.koder.stock.coreservice.manager.StockTradeOrderManager;
import com.koder.stock.coreservice.util.SequenceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockTradeOrderManagerImpl implements StockTradeOrderManager {

    @Autowired
    private StockTradeOrderDOMapper stockTradeOrderDOMapper;
    @Autowired
    private GlobalConfig globalConfig;

    @Override
    @Transactional
    public List<StockTradeOrderDTO> batchAdd(List<StockTradeOrderDTO> stockTradeOrders) {
        List<StockTradeOrderDO> tradeOrders = stockTradeOrders.stream()
                .map(stockTradeOrderDTO -> StockTradeOrderConvertor.fromDTO(stockTradeOrderDTO)).collect(Collectors.toList());

        tradeOrders.forEach(tradeOrder -> {
            tradeOrder.setTradeOrderNo(SequenceUtil.genTradeOrderNum());
            tradeOrder.setUserId(globalConfig.getSystemDefaultUserId());
        });
        stockTradeOrderDOMapper.batchAdd(tradeOrders);
        return tradeOrders.stream().map(tradeOrder -> StockTradeOrderConvertor.fromDO(tradeOrder)).collect(Collectors.toList());
    }

    @Override
    public List<StockTradeOrderDTO> queryUnfinishedTradeOrders(Long userId, String stockCode) {
        List<StockTradeOrderDO> orders = stockTradeOrderDOMapper
                .queryUserOrdersByStatus(userId, stockCode,StockEntrustStatus.getUnfinishedStatus());

        List<StockTradeOrderDTO> orderDTOs = orders.stream().map(stockTradeOrderDO ->
                StockTradeOrderConvertor.fromDO(stockTradeOrderDO)).collect(Collectors.toList());

        return orderDTOs;
    }

    @Override
    public StockTradeOrderDTO queryOrderByTradeNo(String tradeNo) {
        StockTradeOrderDO stockTradeOrderDO = stockTradeOrderDOMapper.queryOrderByTradeNo(tradeNo);
        return StockTradeOrderConvertor.fromDO(stockTradeOrderDO);
    }

    @Override
    public StockTradeOrderDTO queryOrderByOuterTradeNo(String outerTradeNo) {
        StockTradeOrderDO stockTradeOrderDO = stockTradeOrderDOMapper.queryOrderByOuterTradeNo(outerTradeNo);
        if (stockTradeOrderDO == null) {
            return null;
        }
        return StockTradeOrderConvertor.fromDO(stockTradeOrderDO);
    }

    @Override
    public void updateEntrustStatus(StockTradeOrderDTO stockTradeOrderDTO) {
        StockTradeOrderDO dbDO = stockTradeOrderDOMapper.queryOrderById(stockTradeOrderDTO.getId());
        String features = dbDO.getFeatures();
        Map<String, String> featuresMap = new HashMap<>();
        if (!StringUtils.isEmpty(features)) {
            featuresMap = JSON.parseObject(features, HashMap.class);
        }
        featuresMap.putAll(stockTradeOrderDTO.getFeatures());
        features = JSON.toJSONString(featuresMap);
        stockTradeOrderDOMapper.updateEntrustStatus(stockTradeOrderDTO.getId(), stockTradeOrderDTO.getStatus(), stockTradeOrderDTO.getOuterTradeNo(), features);
    }

    @Override
    public void updateEntrustCallBack(StockTradeOrderDTO stockTradeOrderDTO) {
        StockTradeOrderDO stockTradeOrderDO = StockTradeOrderConvertor.fromDTO(stockTradeOrderDTO);
        stockTradeOrderDOMapper.updateEntrustCallback(stockTradeOrderDO);
    }

    @Override
    public List<StockTradeOrderDTO> queryRecreateOrders(String market) {
        List<StockTradeOrderDO> orders = stockTradeOrderDOMapper.queryReCreateOrder(market);
        List<StockTradeOrderDTO> orderDTOs = orders.stream().map(stockTradeOrderDO ->
                StockTradeOrderConvertor.fromDO(stockTradeOrderDO)).collect(Collectors.toList());
        return orderDTOs;
    }

    @Override
    public void updateReCreatedStatus(List<Long> orderIdList, String fromStatus, String toStatus) {
        stockTradeOrderDOMapper.updateReCreatedStatus(orderIdList,fromStatus,toStatus);
    }
}
