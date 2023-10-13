package com.koder.stock.coreservice.handler.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.koder.stock.client.dto.*;
import com.koder.stock.coreservice.domain.constant.*;
import com.koder.stock.coreservice.handler.StrategyHandler;
import com.koder.stock.coreservice.manager.StockBasicManager;
import com.koder.stock.coreservice.manager.StockTradeOrderManager;
import com.koder.stock.coreservice.thirdparty.hs.HsApiProxy;
import com.koder.stock.coreservice.util.PriceGapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class NetworkStrategyHandlerImpl implements StrategyHandler {

    @Autowired
    private StockTradeOrderManager stockTradeOrderManager;

    @Autowired
    private StockBasicManager stockBasicManager;

    @Autowired // FIXME : 不应该放这里
    private HsApiProxy hsApiProxy;

    private Logger logger = LoggerFactory.getLogger(NetworkStrategyHandlerImpl.class);


    /**
     * 证券价格范围0.01-0.25 最小单位差价0.001
     * 证券价格范围0.25-0.50 最小单位差价0.005
     * 证券价格范围0.50-10.00 最小单位差价0.01
     * 证券价格范围10.00-20.00 最小单位差价0.02
     * 证券价格范围20.00-100.00 最小单位差价0.05
     * 证券价格范围100.00-200.00 最小单位差价0.10
     * 证券价格范围200.00-500.00 最小单位差价0.20
     * 证券价格范围500.00-1000.00 最小单位差价0.50
     *
     * @return
     */
    @Override
    public String getCode() {
        return StockStrategyType.DYNAMIC_BASE_PRICE;
    }

    public final static String entrustType = StockEntrustTypeConstant.LIMIT_PRICE;
    public final static String TRADE_WHEN_CLOSE = "1";

    @Override
    public List<StockTradeOrderDTO> buildTradeOrder(StockTradeStrategyDTO stockTradeStrategyDTO, StockQuotationDTO stockQuotationDTO) {

        Long currentTime = Calendar.getInstance().getTimeInMillis();
        // 确定在生效时间范围内
        if (currentTime > stockTradeStrategyDTO.getEndTime() || currentTime < stockTradeStrategyDTO.getStartTime()) {
            logger.warn("strategy has expired or inactivated:{}", JSON.toJSONString(stockTradeStrategyDTO));
            return Lists.newArrayList();
        }
        // 确定状态是否正常
        if (!stockTradeStrategyDTO.getStatus().equals(StockBasicStatus.NORMAL)) {
            logger.warn("strategy status inactivated:{}", JSON.toJSONString(stockTradeStrategyDTO));
            return Lists.newArrayList();
        }
        // 解析条件
        NetworkStrategyConditionDTO conditionDTO = JSON.parseObject(stockTradeStrategyDTO.getStrategyContent(), NetworkStrategyConditionDTO.class);

        logger.warn("strategy conditionDTO,stock code[{}],[{}]", stockQuotationDTO.getCode(), stockTradeStrategyDTO.getStrategyContent());

        BigDecimal minPrice = new BigDecimal(conditionDTO.getMinimumPrice());
        BigDecimal maxPrice = new BigDecimal(conditionDTO.getMaximumPrice());

        if (minPrice.compareTo(stockQuotationDTO.getLatestPrice()) > 0 || maxPrice.compareTo(stockQuotationDTO.getLatestPrice()) < 0) {
            logger.warn("strategy price not matched[{}],[{}],latest price [{}]", stockQuotationDTO.getCode(), JSON.toJSONString(conditionDTO), stockQuotationDTO.getLatestPrice().toPlainString());
            return Lists.newArrayList();
        }

        List<StockTradeOrderDTO> stockTradeOrders = stockTradeOrderManager.queryUnfinishedTradeOrders(stockTradeStrategyDTO.getUserId(), stockQuotationDTO.getCode());

        List<StockTradeOrderDTO> stockBuyInTradeOrders = stockTradeOrders.stream()
                .filter(stockTradeOrderDTO -> stockTradeOrderDTO.getEntrustBs().equals(StockEntrustBS.BUY_IN))
                .collect(Collectors.toList());

        logger.warn("unfinished count of order [{}], Code[{}]", stockBuyInTradeOrders.size(),stockQuotationDTO.getCode());

        if (!checkWhetherLessThanMaxOrders(stockBuyInTradeOrders, conditionDTO)) {
            logger.warn("strategy order count not matched[{}],[{}],unfinished count [{}]", stockQuotationDTO.getCode(), JSON.toJSONString(conditionDTO), stockTradeOrders.size());
            return Lists.newArrayList();
        }
        List<StockTradeOrderDTO> tradeOrders = calculateTradeOrder(stockQuotationDTO, conditionDTO, stockBuyInTradeOrders);
        tradeOrders.stream().forEach(tradeOrder -> tradeOrder.setStrategyId(stockTradeStrategyDTO.getId()));
//        // 持仓数量判断
        Map<String, StockHoldingDTO> stockHoldingMap = hsApiProxy.queryHolding(tradeOrders.get(0).getExchangeType());
        StockHoldingDTO stockHoldingDTO = stockHoldingMap.get(stockQuotationDTO.getCode());
        if (stockHoldingDTO == null) {
            return tradeOrders;
        }
        // 如果持仓的数量超过多少阈值，不新建单
        Integer limitedCount = conditionDTO.getMaxHoldingCount();
        if (limitedCount == null) {
            return tradeOrders;
        }
        Integer expectedCount = stockHoldingDTO.getCurrentCount().intValue() + tradeOrders.get(0).getEntrustVolume();
        if (expectedCount > limitedCount) {
            return Lists.newArrayList();
        }
        return tradeOrders;
    }

    private boolean checkWhetherLessThanMaxOrders(List<StockTradeOrderDTO> stockTradeOrders, NetworkStrategyConditionDTO conditionDTO) {
        List<StockTradeOrderDTO> stockBuyInTradeOrders = stockTradeOrders.stream()
                .filter(stockTradeOrderDTO -> stockTradeOrderDTO.getEntrustBs().equals(StockEntrustBS.BUY_IN))
                .collect(Collectors.toList());
        if (stockBuyInTradeOrders.size() >= conditionDTO.getMaxUnfinishedOrderCount()) {
            return false;
        }
        return true;
    }

    private List<StockTradeOrderDTO> calculateTradeOrder(StockQuotationDTO stockQuotationDTO, NetworkStrategyConditionDTO conditionDTO, List<StockTradeOrderDTO> orders) {
        TradeOrderPriceCalculationDTO tradeOrderPriceCalculationDTO = TradeOrderPriceCalculationDTO.builder()
                .openPrice(stockQuotationDTO.getOpeningPrice())
                .lastClosingPrice(stockQuotationDTO.getLastClosingPrice())
                .latestPrice(stockQuotationDTO.getLatestPrice())
                .code(stockQuotationDTO.getCode())
                .build();
        tradeOrderPriceCalculationDTO.setOrderLowestPrice(getLowestPriceOfOrders(orders));
        tradeOrderPriceCalculationDTO.setOrderHighestPrice(getHighestPriceOfOrders(orders));
        List<StockTradeOrderDTO> newOrders = calculateTradeOrderPrice(conditionDTO, tradeOrderPriceCalculationDTO);
        return newOrders;
    }

    private List<StockTradeOrderDTO> calculateTradeOrderPrice(NetworkStrategyConditionDTO conditionDTO, TradeOrderPriceCalculationDTO tradeOrderPriceCalculationDTO) {
        StockBasicDTO stockBasicDTO = stockBasicManager.queryByCode(tradeOrderPriceCalculationDTO.getCode());
        StockTradeOrderDTO initStockTradeOrderDTO = StockTradeOrderDTO.builder().stockCode(tradeOrderPriceCalculationDTO.getCode())
                .strategyType(getCode()).exchangeType(StockExchangeTypeConstant.exchangeTypeMap.get(stockBasicDTO.getMarket()))
                .exchange(stockBasicDTO.getExchange()).tradeWhenClose(TRADE_WHEN_CLOSE).build();

        initStockTradeOrderDTO.putIfAbsent(StockTradeOrderFeatureKeys.REF_PRICE, tradeOrderPriceCalculationDTO.getLatestPrice().toPlainString());
        initStockTradeOrderDTO.setReCreate(StockOrderReCreateStatus.UN_NEED);
        // 计算买入交易单
        StockTradeOrderDTO buyInTradeOrder = calculateEntrustDetails(conditionDTO, tradeOrderPriceCalculationDTO, initStockTradeOrderDTO);
        if (buyInTradeOrder == null) {
            return Lists.newArrayList();
        }
        return Lists.newArrayList(buyInTradeOrder);
    }

    public StockTradeOrderDTO calculateEntrustDetails(NetworkStrategyConditionDTO conditionDTO, TradeOrderPriceCalculationDTO tradeOrderPriceCalculationDTO,
                                                      StockTradeOrderDTO stockTradeOrderDTO) {
        // 空仓买入，价格是开盘价或者当前价 减掉规则中的比例
        if (tradeOrderPriceCalculationDTO.getOrderHighestPrice() == null) {
            BigDecimal lowerPrice = tradeOrderPriceCalculationDTO.getLatestPrice();
            // 最新价格与开盘价之间的价差是否达到了condition
            BigDecimal dropValue = lowerPrice.multiply(new BigDecimal(conditionDTO.getBuyInDropPt()));
            BigDecimal expectedPrice = lowerPrice.subtract(dropValue).setScale(2, RoundingMode.HALF_UP);
            stockTradeOrderDTO.setStatus(StockEntrustStatus.INIT_ED);
            stockTradeOrderDTO.setEntrustType(entrustType);
            stockTradeOrderDTO.setEntrustVolume(conditionDTO.getEntrustCount());
            stockTradeOrderDTO.setEntrustBs(StockEntrustBS.BUY_IN);
            stockTradeOrderDTO.setEntrustPrice(PriceGapUtil.calculatePriceGap(stockTradeOrderDTO.getExchangeType(), expectedPrice));
            return stockTradeOrderDTO;
        }
        // 计算方向：
        // 当前价 大于 未完成订单中的最低价。不建单，退下
        if (tradeOrderPriceCalculationDTO.getLatestPrice().compareTo(tradeOrderPriceCalculationDTO.getOrderLowestPrice()) >= 0) {
            return null;
        }
        // 当前价 小于未完成订单中的最低价，求期望价格。第二个订单下跌双倍的比例。
        BigDecimal dropValue = tradeOrderPriceCalculationDTO.getOrderLowestPrice().multiply(new BigDecimal(conditionDTO.getBuyInDropPt()));
        BigDecimal expectedPrice = tradeOrderPriceCalculationDTO.getOrderLowestPrice().subtract(dropValue).setScale(2, RoundingMode.HALF_UP);

        BigDecimal lowerPrice = expectedPrice.compareTo(tradeOrderPriceCalculationDTO.getLatestPrice()) < 0
                ? expectedPrice : tradeOrderPriceCalculationDTO.getLatestPrice();
        // 当前价如果不在最小价格区间范围内，不建单，退出
        if (lowerPrice.compareTo(new BigDecimal(conditionDTO.getMinimumPrice())) < 0) {
            return null;
        }
        stockTradeOrderDTO.setStatus(StockEntrustStatus.INIT_ED);
        stockTradeOrderDTO.setEntrustType(entrustType);
        stockTradeOrderDTO.setEntrustVolume(conditionDTO.getEntrustCount());
        stockTradeOrderDTO.setEntrustBs(StockEntrustBS.BUY_IN);
        stockTradeOrderDTO.setEntrustPrice(PriceGapUtil.calculatePriceGap(stockTradeOrderDTO.getExchangeType(), lowerPrice));
        return stockTradeOrderDTO;
    }

    private BigDecimal getLowestPriceOfOrders(List<StockTradeOrderDTO> orders) {
        if (CollectionUtils.isEmpty(orders)) {
            return null;
        }
        Optional<StockTradeOrderDTO> minPrice = orders.stream().min(Comparator.comparing(StockTradeOrderDTO::getEntrustPrice));
        return minPrice.get().getEntrustPrice();
    }

    private BigDecimal getHighestPriceOfOrders(List<StockTradeOrderDTO> orders) {
        if (CollectionUtils.isEmpty(orders)) {
            return null;
        }
        Optional<StockTradeOrderDTO> maxPrice = orders.stream().max(Comparator.comparing(StockTradeOrderDTO::getEntrustPrice));
        return maxPrice.get().getEntrustPrice();
    }
}
