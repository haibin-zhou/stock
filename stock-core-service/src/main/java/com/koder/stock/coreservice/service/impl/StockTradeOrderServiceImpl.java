package com.koder.stock.coreservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.koder.stock.client.dto.*;
import com.koder.stock.coreservice.domain.constant.*;
import com.koder.stock.coreservice.handler.StrategyHandler;
import com.koder.stock.coreservice.handler.impl.NetworkStrategyHandlerImpl;
import com.koder.stock.coreservice.listenner.StockTradeOrderCreationMsgListener;
import com.koder.stock.coreservice.manager.StockBasicManager;
import com.koder.stock.coreservice.manager.StockTradeOrderManager;
import com.koder.stock.coreservice.manager.StockTradeStrategyManager;
import com.koder.stock.coreservice.service.StockTradeOrderService;
import com.koder.stock.coreservice.util.LockUtil;
import com.koder.stock.coreservice.util.PriceGapUtil;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.koder.stock.coreservice.domain.constant.StockTradeOrderFeatureKeys.REF_TRADE_ORDER_NO;
import static com.koder.stock.coreservice.domain.constant.StockTradeOrderFeatureKeys.RE_CREATE_ORDER_NO;

@Service
public class StockTradeOrderServiceImpl implements StockTradeOrderService {

    @Autowired
    private StockBasicManager stockBasicManager;
    @Autowired
    private StockTradeStrategyManager stockTradeStrategyManager;
    @Autowired
    private StockTradeOrderManager stockTradeOrderManager;
    @Autowired
    private StockTradeOrderCreationMsgListener stockTradeOrderCreationMsgListener;
    @Resource
    private TransactionTemplate transactionTemplate;

    private Map<String, StrategyHandler> handlerMap;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final Map<String, AtomicInteger> concurrentControlMap = new ConcurrentHashMap<>();

    private static final Map<String,String> reCreateMap = new HashMap<>();
    /**
     * 0	No Register(未报)
     * 1	Wait to Register(待报)
     * 2	Host Registered(已报)
     * 3	Wait for Cancel(已报待撤)
     * 4	Wait for Cancel(Partially Matched)(部成待撤)
     * 5	Partially Cancelled(部撤)
     * 6	Cancelled(已撤)
     * 7	Partially Filled(部成)
     * 8	Filled(已成)
     * 9	Host Reject(废单)
     * A	Wait for Modify (Registed)（已报待改）
     * B	Unregistered（无用）
     * C	Registering（无用）
     * D	Revoke Cancel（无用）
     * W	Wait for Confirming（待确认）
     * X	Pre Filled（无用）
     * E	Wait for Modify (Partially Matched)（部成待改）
     * F	Reject（预埋单检查废单）
     * G	Cancelled(Pre-Order)（预埋单已撤）
     * H	Wait for Review（待审核）
     * J	Review Fail（审核失败）
     * */
    static {
        reCreateMap.put("9",StockOrderReCreateStatus.WAIT_RE_CREATE);
        reCreateMap.put("6",StockOrderReCreateStatus.WAIT_RE_CREATE);
    }

    @Autowired
    public void setStrategyHandlerList(List<StrategyHandler> handlerList) {
        if (CollectionUtils.isEmpty(handlerList)) {
            return;
        }
        if (handlerMap == null) {
            handlerMap = new HashMap<>();
        }
        handlerList.stream().forEach(handler -> {
            handlerMap.put(handler.getCode(), handler);
        });
    }

    @Override
    public List<StockTradeOrderDTO> createTradeOrders(StockQuotationDTO stockQuotationDTO) {
        // 并发处理
        logger.warn("创建订单开始：[{}]", JSON.toJSONString(stockQuotationDTO));
        boolean lock = LockUtil.getLock(LockUtil.CREATE_ORDER_LOCKED, stockQuotationDTO.getCode());
        if (!lock) {
            logger.warn("并发控制返回：[{}]", JSON.toJSONString(stockQuotationDTO));
            return Lists.newArrayList();
        }
        // 查询股票信息
        try {
            StockBasicDTO stockBasicDTO = stockBasicManager.queryByCode(stockQuotationDTO.getCode());
            if (!stockBasicDTO.getStatus().equals(StockBasicStatus.NORMAL)) {
                logger.warn("股票状态不正确，返回：[{}]", stockBasicDTO.getStatus());
                return new ArrayList<>();
            }
            // 查询策略数据
            List<StockTradeStrategyDTO> strategies = stockTradeStrategyManager.queryStrategyByStockId(stockBasicDTO.getId());
            if (CollectionUtils.isEmpty(strategies)) {
                logger.warn("策略数据为空，返回：[{}]", stockBasicDTO.getCode());
                return new ArrayList<>();
            }

            final List<StockTradeOrderDTO> tradeOrders = new ArrayList<>();

            strategies.stream().forEach(strategy -> {
                StrategyHandler strategyHandler = handlerMap.get(strategy.getStrategyType());
                boolean tradeLock = LockUtil.getLock(LockUtil.CALCULATE_ORDER, strategy.getId() + "");
                try {
                    if (tradeLock) {
                        List<StockTradeOrderDTO> orderDTOs = strategyHandler.buildTradeOrder(strategy, stockQuotationDTO);
                        tradeOrders.addAll(orderDTOs);
                    } else {
                        logger.warn("订单创建并发控制，不创建，订单策略ID:{}", strategy.getId());
                    }
                } finally {
                    LockUtil.releaseLock(LockUtil.CALCULATE_ORDER, strategy.getId() + "", true);
                }
            });

            if (CollectionUtils.isEmpty(tradeOrders)) {
                logger.warn("创建订单为空，返回：[{}]", stockBasicDTO.getCode());
                return new ArrayList<>();
            }
            List<StockTradeOrderDTO> transitOrders = stockTradeOrderManager.batchAdd(tradeOrders);
            logger.warn("创建订单成功，股票代码[{}],订单数量[{}]", stockBasicDTO.getCode(), transitOrders.size());
            transitOrders.stream().forEach(tradeOrder -> {
                stockTradeOrderCreationMsgListener.orderCreationMsg(tradeOrder);
            });
            return transitOrders;
        } catch (Exception e) {
            logger.error("创建订单发生异常，股票代码[{}]", stockQuotationDTO.getCode(), e);
        } finally {
            LockUtil.releaseLock(LockUtil.CREATE_ORDER_LOCKED, stockQuotationDTO.getCode(), false);
        }
        return Lists.newArrayList();
    }

    @Override
    @Transactional
    public void entrustCallback(StockTradeOrderDTO stockTradeOrderDTO) {
        StockTradeOrderDTO dbDTO = stockTradeOrderManager.queryOrderByOuterTradeNo(stockTradeOrderDTO.getOuterTradeNo());
        if (dbDTO == null) {
            throw new RuntimeException("stock trade order has not been updated");
        }
        // 需要重建的订单，把状态切换成待重建
        if(StockOrderReCreateStatus.RE_CREATE_NEEDED.equals(dbDTO.getReCreate())){
            if (reCreateMap.get(stockTradeOrderDTO.getStatus()) != null) {
                dbDTO.setReCreate(reCreateMap.get(stockTradeOrderDTO.getStatus()));
            }
        }
        dbDTO.setStatus(stockTradeOrderDTO.getStatus());
        dbDTO.setDealDateTime(stockTradeOrderDTO.getDealDateTime());
        dbDTO.setDealCount(stockTradeOrderDTO.getDealCount());
        dbDTO.setDealPrice(stockTradeOrderDTO.getDealPrice());
        dbDTO.addFeatures(stockTradeOrderDTO.getFeatures());
        stockTradeOrderManager.updateEntrustCallBack(dbDTO);
        // 构建止盈止损订单
        StockTradeOrderDTO stopOrder = createByPreOrder(dbDTO);
        if (stopOrder != null) {
            stockTradeOrderCreationMsgListener.orderCreationMsg(stopOrder);
        }
    }

    @Override
    public ResultDTO<StockTradeOrderDTO> createTradeOrder(StockTradeOrderDTO stockTradeOrderDTO) {
        if (stockTradeOrderDTO == null) {
            return ResultDTO.of("stock.order.required", "股票订单不能为空");
        }
        StockBasicDTO stockBasicDTO = stockBasicManager.queryByCode(stockTradeOrderDTO.getStockCode());
        if (stockBasicDTO == null) {
            return ResultDTO.of("stock.code.required", "股票不存在，代码[" + stockTradeOrderDTO.getStockCode() + "]");
        }
        if (StringUtils.isBlank(stockTradeOrderDTO.getEntrustBs()) || StringUtils.isBlank(stockTradeOrderDTO.getEntrustBs())) {
            return ResultDTO.of("stock.entrust.required", "委托参数不完整，代码[" + stockTradeOrderDTO.getStockCode() + "]");
        }
        if (stockTradeOrderDTO.getEntrustVolume() <= 0) {
            return ResultDTO.of("stock.entrust.required", "委托参数不完整，代码[" + stockTradeOrderDTO.getStockCode() + "]");
        }

        String exchangeType = StockExchangeTypeConstant.getExchangeType(stockBasicDTO.getMarket());
        stockTradeOrderDTO.setExchangeType(exchangeType);
        stockTradeOrderDTO.setStrategyType(StockStrategyType.MANUAL);
        stockTradeOrderDTO.setStrategyId(0L);
        stockTradeOrderDTO.setExchange(stockBasicDTO.getExchange());
        stockTradeOrderDTO.setTradeWhenClose("1");
        stockTradeOrderDTO.setEntrustType(StockEntrustTypeConstant.LIMIT_PRICE);
        stockTradeOrderDTO.setStatus(StockEntrustStatus.INIT_ED);

        List<StockTradeOrderDTO> transitOrders = stockTradeOrderManager.batchAdd(Lists.newArrayList(stockTradeOrderDTO));
        if (CollectionUtils.isEmpty(transitOrders)) {
            return ResultDTO.of("create.order.error", "no order creation");
        }

        stockTradeOrderCreationMsgListener.orderCreationMsg(transitOrders.get(0));

        return ResultDTO.of(transitOrders.get(0));
    }

    private StockTradeOrderDTO createByPreOrder(StockTradeOrderDTO stockTradeOrderDTO) {
        // 先查策略
        Long strategyId = stockTradeOrderDTO.getStrategyId();
        StockTradeStrategyDTO stockTradeStrategyDTO = stockTradeStrategyManager.queryStrategyById(strategyId);
        NetworkStrategyConditionDTO conditionDTO = JSON.parseObject(stockTradeStrategyDTO.getStrategyContent(), NetworkStrategyConditionDTO.class);

        if (stockTradeOrderDTO.getEntrustBs().equals(StockEntrustBS.BUY_IN) && stockTradeOrderDTO.getStatus().equals(StockEntrustStatus.SUCCESS)) {
            // 计算卖出交易单
            BigDecimal selloutPrice = stockTradeOrderDTO.getDealPrice()
                    .add(stockTradeOrderDTO.getDealPrice()
                            .multiply(new BigDecimal(conditionDTO.getSellOutRisePt())))
                    .setScale(2, RoundingMode.HALF_EVEN);

            StockTradeOrderDTO sellerStockTradeOrderDTO = StockTradeOrderDTO.builder().stockCode(stockTradeOrderDTO.getStockCode())
                    .strategyType(stockTradeStrategyDTO.getStrategyType()).exchangeType(stockTradeOrderDTO.getExchangeType())
                    .exchange(stockTradeOrderDTO.getExchange()).tradeWhenClose(stockTradeOrderDTO.getTradeWhenClose()).build();

            sellerStockTradeOrderDTO.setStatus(StockEntrustStatus.INIT_ED);
            sellerStockTradeOrderDTO.setEntrustType(NetworkStrategyHandlerImpl.entrustType);
            sellerStockTradeOrderDTO.setEntrustVolume(conditionDTO.getEntrustCount());
            sellerStockTradeOrderDTO.setEntrustPrice(PriceGapUtil.calculatePriceGap(stockTradeOrderDTO.getExchangeType(), selloutPrice));
            sellerStockTradeOrderDTO.setStrategyId(stockTradeStrategyDTO.getId());

            if(conditionDTO.getReCreate()!=null){
                sellerStockTradeOrderDTO.setReCreate(conditionDTO.getReCreate()?StockOrderReCreateStatus.RE_CREATE_NEEDED:StockOrderReCreateStatus.UN_NEED);
            }
            sellerStockTradeOrderDTO.setEntrustBs(StockEntrustBS.SELL_OUT);
            sellerStockTradeOrderDTO.putIfAbsent(REF_TRADE_ORDER_NO, stockTradeOrderDTO.getTradeOrderNo());
            List<StockTradeOrderDTO> transitOrders = stockTradeOrderManager.batchAdd(Lists.newArrayList(sellerStockTradeOrderDTO));
            if (!CollectionUtils.isEmpty(transitOrders)) {
                return transitOrders.get(0);
            }
        }
        return null;
    }

    @Override
    public void entrustStatusUpdate(StockTradeOrderDTO stockTradeOrderDTO) {
        stockTradeOrderManager.updateEntrustStatus(stockTradeOrderDTO);
    }

    @Override
    public List<StockTradeOrderDTO> queryUnEntrustedOrders(Long userId, String stockCode, String entrustBs) {
        List<StockTradeOrderDTO> stockTradeOrder = stockTradeOrderManager.queryUnfinishedTradeOrders(userId, stockCode);
        List<StockTradeOrderDTO> result = stockTradeOrder.stream()
                .filter(stockTradeOrderDTO -> stockTradeOrderDTO.getEntrustBs().equals(entrustBs))
                .filter(stockTradeOrderDTO -> stockTradeOrderDTO.getStatus().equals(StockEntrustStatus.INIT_ED))
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public void reCreateOrder(String market) {
        // 先查询市场中是否有需要重建的订单
        List<StockTradeOrderDTO> stockTradeOrders = stockTradeOrderManager.queryRecreateOrders(market);
        if(CollectionUtils.isEmpty(stockTradeOrders)){
            logger.warn("没有需要重新创建的订单，返回");
            return;
        }
        List<StockTradeOrderDTO> savedOrders = new ArrayList<>();
        List<Long> reCreatedIds = new ArrayList<>();
        for (StockTradeOrderDTO stockTradeOrder : stockTradeOrders) {
            StockTradeOrderDTO reCreatedStockTradeOrderDTO = StockTradeOrderDTO.builder().stockCode(stockTradeOrder.getStockCode())
                    .strategyType(stockTradeOrder.getStrategyType())
                    .exchange(stockTradeOrder.getExchange()).tradeWhenClose(stockTradeOrder.getTradeWhenClose()).build();
            reCreatedStockTradeOrderDTO.setStatus(StockEntrustStatus.INIT_ED);
            reCreatedStockTradeOrderDTO.setEntrustType(stockTradeOrder.getEntrustType());
            reCreatedStockTradeOrderDTO.setEntrustVolume(stockTradeOrder.getEntrustVolume());
            reCreatedStockTradeOrderDTO.setEntrustPrice(stockTradeOrder.getEntrustPrice());
            reCreatedStockTradeOrderDTO.setStrategyId(stockTradeOrder.getStrategyId());
            reCreatedStockTradeOrderDTO.setReCreate(StockOrderReCreateStatus.RE_CREATE_NEEDED);
            reCreatedStockTradeOrderDTO.setEntrustBs(stockTradeOrder.getEntrustBs());
            reCreatedStockTradeOrderDTO.putIfAbsent(RE_CREATE_ORDER_NO, stockTradeOrder.getTradeOrderNo());
            savedOrders.add(reCreatedStockTradeOrderDTO);
            reCreatedIds.add(stockTradeOrder.getId());
        }
        List<StockTradeOrderDTO> transitOrders = new ArrayList<>();

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                transitOrders.addAll(stockTradeOrderManager.batchAdd(Lists.newArrayList(savedOrders)));
                stockTradeOrderManager.updateReCreatedStatus(reCreatedIds,StockOrderReCreateStatus.WAIT_RE_CREATE,StockOrderReCreateStatus.RE_CREATED);
            }
        });
        transitOrders.stream().forEach(stockTradeOrderDTO -> {
            stockTradeOrderCreationMsgListener.orderCreationMsg(stockTradeOrderDTO);
        });

    }
}