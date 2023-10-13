package com.koder.stock.coreservice.thirdparty.hs;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.huasheng.quant.open.api.HSQuantOpenApiHandle;
import com.huasheng.quant.open.sdk.domain.ModelResult;
import com.huasheng.quant.open.sdk.vo.*;
import com.koder.stock.client.dto.*;
import com.koder.stock.coreservice.domain.constant.StockEntrustBS;
import com.koder.stock.coreservice.domain.constant.StockEntrustStatus;
import com.koder.stock.coreservice.domain.constant.StockTradeOrderFeatureKeys;
import com.koder.stock.coreservice.service.StockBasicInformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HsApiProxy {

    private HSQuantOpenApiHandle hsQuotationApi;
    @Autowired
    private StockBasicInformationService stockBasicInformationService;

    private HSQuantOpenApiHandle hsTradeApi;

    @Autowired
    private HSCommunicationKeys hsCommunicationKeys;
    @Autowired
    private HsMessageNotifyHandle hsMessageNotifyHandle;

    private Logger logger = LoggerFactory.getLogger("HsApiProxy");

    @PostConstruct
    public void init() {
        this.hsQuotationApi = new HSQuantOpenApiHandle(hsCommunicationKeys.getDomainUrl(), hsCommunicationKeys.getResourcePath(), hsMessageNotifyHandle, true);
        this.hsTradeApi = new HSQuantOpenApiHandle(hsCommunicationKeys.getDomainUrl(), hsCommunicationKeys.getResourcePath(), hsMessageNotifyHandle, false);
        // 订阅行情
        if (hsCommunicationKeys.getInitQuotationApi()) {
            listenQuotation();
        }
        // 订阅交易状态变更
        if (hsCommunicationKeys.getInitTradeApi()) {
            listenTradeOrderStatus();
        }
    }

    public ResultDTO<StockTradeOrderDTO> entrustOrder(StockTradeOrderDTO stockTradeOrderDTO) {
        if (!stockTradeOrderDTO.getStatus().equals(StockEntrustStatus.EN_TRUSTING)) {
            return ResultDTO.of(stockTradeOrderDTO);
        }
        try {
            TradeEntrustParam tradeEntrustParam = new TradeEntrustParam();
            tradeEntrustParam.setEntrustBs(stockTradeOrderDTO.getEntrustBs());
            tradeEntrustParam.setEntrustPrice(stockTradeOrderDTO.getEntrustPrice().toPlainString());
            tradeEntrustParam.setEntrustType(stockTradeOrderDTO.getEntrustType());
            tradeEntrustParam.setEntrustAmount(stockTradeOrderDTO.getEntrustVolume() + "");
            if (!StringUtils.isEmpty(stockTradeOrderDTO.getExchange())) {
                tradeEntrustParam.setExchange(stockTradeOrderDTO.getExchange());
            }
            tradeEntrustParam.setExchangeType(stockTradeOrderDTO.getExchangeType());
            tradeEntrustParam.setStockCode(stockTradeOrderDTO.getStockCode());
            tradeEntrustParam.setValidDays("1"); // FIXME : fix valid days
            tradeEntrustParam.setSessionType("3"); // 条件单
            logger.warn("trade entrust param:[{}]", JSON.toJSONString(tradeEntrustParam));
            ModelResult<String> result = this.hsTradeApi.tradeEntrust(tradeEntrustParam);
            if (result.isSuccess()) {
                stockTradeOrderDTO.setStatus(StockEntrustStatus.EN_TRUSTED);
                stockTradeOrderDTO.setOuterTradeNo(result.getModel());
            } else {
                logger.warn("trade entrust status failed:trade no-[{}],msg-[{}]", stockTradeOrderDTO.getTradeOrderNo(), result.getErrorCode() + ":" + result.getErrorMsg());
                stockTradeOrderDTO.setStatus(StockEntrustStatus.EN_TRUSTED_FAILED);
                stockTradeOrderDTO.putIfAbsent(StockTradeOrderFeatureKeys.FAIL_REASON_CODE, result.getErrorCode());
                stockTradeOrderDTO.putIfAbsent(StockTradeOrderFeatureKeys.FAIL_REASON_MSG, result.getErrorMsg());
            }
        } catch (Exception e) {
            logger.warn("stock trade entrusted failed,tradeNo[{}]", stockTradeOrderDTO.getTradeOrderNo(), e);
        }
        return ResultDTO.of(stockTradeOrderDTO);
    }

    private void listenTradeOrderStatus() {
        String token = hsTradeApi
                .httpLoginAndGetToken(hsCommunicationKeys.getCountryCode(), hsCommunicationKeys.getMobile(), hsCommunicationKeys.getPassword(), false);
        boolean tradeConnection = this.hsTradeApi.startConnect(token, hsCommunicationKeys.getTradePwd());
        logger.warn("tradeConnection:[{}]", tradeConnection);
        ModelResult<Boolean> result = hsTradeApi.tradeSubscribe();
        if (!result.isSuccess()) {
            logger.warn("trade subscribe failed:[{}]", result.getErrorCode() + ":" + result.getErrorMsg());
        }
        logger.warn("trade subscribe success");
    }

    private void listenQuotation() {
        String token = hsQuotationApi.httpLoginAndGetToken(hsCommunicationKeys.getCountryCode(), hsCommunicationKeys.getMobile(), hsCommunicationKeys.getPassword(), false);
        logger.warn("got token from hs:{}, next step start connect", token);
        boolean hqConnection = this.hsQuotationApi.startConnect(token, hsCommunicationKeys.getTradePwd());
        logger.warn("hqConnection:[{}]", hqConnection);
        List<StockBasicDTO> stockBasics = stockBasicInformationService.queryAllStocks();
        Map<Integer, List<String>> stockMap = new HashMap<>();
        stockBasics.stream().forEach(stockBasicDTO -> {
            List<String> codes = stockMap.get(stockBasicDTO.getMarket());
            if (CollectionUtils.isEmpty(codes)) {
                codes = new ArrayList<>();
            }
            codes.add(stockBasicDTO.getCode());
            stockMap.put(stockBasicDTO.getMarket(), codes);
        });
        logger.warn("行情订阅列表:[{}]", JSON.toJSONString(stockMap));
        ModelResult<Boolean> result = hsQuotationApi.hqSubscribe(HsApiTopicIdConstant.BASIC_QOT, stockMap);
        if (!result.isSuccess()) {
            logger.warn("基础行情订阅失败:[{}][{}]", JSON.toJSONString(stockMap), result.getErrorCode() + ":" + result.getErrorMsg());
            return;
        }
        // 订阅买卖档
        result = hsQuotationApi.hqSubscribe(HsApiTopicIdConstant.BUY_SELL_LEVEL, stockMap);
        if (!result.isSuccess()) {
            logger.warn("买卖档行情订阅失败:[{}][{}]", JSON.toJSONString(stockMap), result.getErrorCode() + ":" + result.getErrorMsg());
            return;
        }
    }

    public List<Quotation10LevelDTO> query10LevelQuotation(String stockCode) {
        //int dataType, String code, int mktTmType, Integer depthBookType
        ModelResult<OrderBookVo> result = hsQuotationApi.hqOrderBook(1, stockCode, 1, 2);
        if (!result.isSuccess()) {
            logger.warn("query10LevelQuotation failed, params[{}],Msg[{}]", stockCode, result.getErrorCode() + ":" + result.getErrorMsg());
            return Lists.newArrayList();
        }
        OrderBookVo bookVo = result.getModel();
        List<HqOrderBook> buyInBooks = bookVo.getOrderBookAskList();
        List<HqOrderBook> sellOutBooks = bookVo.getOrderBookBidList();

        List<Quotation10LevelDTO> lvlList = Lists.newArrayList();

        lvlList.addAll(buyInBooks.stream().map(buyInBook -> {
            Quotation10LevelDTO quotation10LevelDTO = Quotation10LevelDTO.builder()
                    .ordersCount(buyInBook.getOrederCount())
                    .brokeId(buyInBook.getBrokeId())
                    .price(new BigDecimal(buyInBook.getPrice()))
                    .volume(buyInBook.getVolume())
                    .level(buyInBook.getLevel())
                    .entrustBs(StockEntrustBS.BUY_IN).build();
            return quotation10LevelDTO;
        }).collect(Collectors.toList()));

        lvlList.addAll(sellOutBooks.stream().map(selloutBook -> {
            Quotation10LevelDTO quotation10LevelDTO = Quotation10LevelDTO.builder()
                    .ordersCount(selloutBook.getOrederCount())
                    .brokeId(selloutBook.getBrokeId())
                    .price(new BigDecimal(selloutBook.getPrice()))
                    .volume(selloutBook.getVolume())
                    .level(selloutBook.getLevel())
                    .entrustBs(StockEntrustBS.SELL_OUT).build();
            return quotation10LevelDTO;
        }).collect(Collectors.toList()));
        return lvlList;
    }

    /**
     * 2	日线
     * 3	周线
     * 4	月线
     * 5	1分钟
     * 6	5分钟
     * 7	15分钟
     * 8	30分钟
     * 9	60分钟
     * 10	120分钟
     * 11	季度线
     * 12	年度线
     * 13	五日
     * 14	3分钟
     * #
     *
     * @param stockCode
     * @param startDate
     * @param cycType
     * @param limit
     * @return
     */

    public List<StockQuotationDTO> queryQuotation(Integer dateType, String stockCode, Long startDate, int cycType, int limit) {
        KLineParam kLineParam = new KLineParam();
        kLineParam.setCode(stockCode);
        kLineParam.setStartDate(startDate);
        kLineParam.setDirection(0); // 往左查0 ， 往右查 1
        /**
         * 2	日线
         * 3	周线
         * 4	月线
         * 5	1分钟
         * 6	5分钟
         * 7	15分钟
         * 8	30分钟
         * 9	60分钟
         * 10	120分钟
         * 11	季度线
         * 12	年度线
         * 13	五日
         * 14	3分钟
         */
        kLineParam.setCycType(cycType);
        kLineParam.setExRightFlag(0); // 0 不复权，1 前复权 2 后复权
        kLineParam.setDataType(dateType);
        kLineParam.setLimit(limit);

        logger.warn("请求历史行情数据参数:[{}]", JSON.toJSONString(kLineParam));

        ModelResult<KLineVo> result = this.hsQuotationApi.hqKLRequest(kLineParam);
        if (!result.isSuccess()) {
            logger.warn("queryQuotation failed, params[{}],Msg[{}]", stockCode, result.getErrorCode() + ":" + result.getErrorMsg());
            return Lists.newArrayList();
        }

        KLineVo kLineVo = result.getModel();
        if (kLineVo == null) {
            logger.warn("queryQuotation empty, params[{}],Msg[{}]", stockCode, result.getErrorCode() + ":" + result.getErrorMsg());
            return Lists.newArrayList();
        }
        logger.warn("queryQuotation data, params[{}],data[{}]", stockCode, JSON.toJSONString(kLineVo));

        List<HqKLine> kLines = kLineVo.getHqKLineList();
        return kLines.stream().map(hqKLine -> {
            StockQuotationDTO stockQuotationDTO = StockQuotationDTO.builder()
                    .code(stockCode).lastClosingPrice(new BigDecimal(hqKLine.getLastClosePrice()).setScale(2, RoundingMode.CEILING))
                    .quotationTime(Instant.ofEpochMilli(hqKLine.getTimestamp()))
                    .highestPrice(new BigDecimal(hqKLine.getHighPrice()).setScale(2, RoundingMode.CEILING))
                    .openingPrice(new BigDecimal(hqKLine.getOpenPrice()).setScale(2, RoundingMode.CEILING))
                    .lowestPrice(new BigDecimal(hqKLine.getLowPrice()).setScale(2, RoundingMode.CEILING))
                    .closingPrice(new BigDecimal(hqKLine.getClosePrice()).setScale(2, RoundingMode.CEILING))
                    .turnOverVolume(new BigDecimal(hqKLine.getVolume()).setScale(2, RoundingMode.CEILING))
                    .turnOverAmount(new BigDecimal(hqKLine.getTurnover()).setScale(2, RoundingMode.CEILING)).build();
            stockQuotationDTO.setChangeAmount(stockQuotationDTO.getHighestPrice().subtract(stockQuotationDTO.getLowestPrice()));
            // 开盘价
            BigDecimal priceRange = stockQuotationDTO.getClosingPrice().subtract(stockQuotationDTO.getOpeningPrice());
            stockQuotationDTO.setChangeRange(priceRange);
            logger.warn("grab stock quotation data:[{}]", JSON.toJSONString(stockQuotationDTO));
            return stockQuotationDTO;
        }).collect(Collectors.toList());
    }


    public StockUserFundDTO getUserFund(String exchangeType) {
        ModelResult<MarginFundInfo> result = hsTradeApi.tradeQueryMargin(exchangeType);
        if (!result.isSuccess()) {
            return null;
        }
        MarginFundInfo fundInfo = result.getModel();
        StockUserFundDTO stockUserFundDTO = StockUserFundDTO.builder()
                .exchangeType(exchangeType)
                .assetBalance(new BigDecimal(fundInfo.getAssetBalance()))
                .availableBalance(new BigDecimal(fundInfo.getBuyPower()))
                .holdingBalance(new BigDecimal(fundInfo.getHoldsBalance()))
                .build();
        return stockUserFundDTO;
    }

    @PreDestroy
    public void destroy() {
        try {
            hsQuotationApi.disconnectAndStop();
            hsTradeApi.disconnectAndStop();
            hsMessageNotifyHandle.shutdown();
        } catch (InterruptedException e) {
            logger.warn("API关闭失败", e);
        }
    }

    public Map<String, StockHoldingDTO> queryHolding(String exchangeType) {
        Map<String, StockHoldingDTO> holdings = new HashMap<>();
        List<StockHoldingDTO> holdList = queryHoldList(exchangeType);
        holdList.stream().forEach(vo -> {
            holdings.put(vo.getStockCode(), vo);
        });
        return holdings;
    }

    @Cacheable(cacheNames = "queryHolding")
    public List<StockHoldingDTO> queryHoldList(String exchangeType) {
        ModelResult<List<HoldsVo>> result = this.hsTradeApi.tradeQueryHoldsList(exchangeType);
        if (!result.isSuccess()) {
            logger.warn("查询持仓列表发生异常，[{}][{}]", result.getErrorCode(), result.getErrorMsg());
            return new ArrayList<>();
        }
        logger.warn("查询持仓列表，[{}]", JSON.toJSONString(result.getModel()));
        List<HoldsVo> holdingVOs = result.getModel();
        if (CollectionUtils.isEmpty(holdingVOs)) {
            return new ArrayList<>();
        }
        List<StockHoldingDTO> holdList = holdingVOs.stream().map(holdsVo -> {
            StockHoldingDTO stockHoldingDTO = StockHoldingDTO.builder()
                    .stockCode(holdsVo.getStockCode())
                    .costPrice(new BigDecimal(holdsVo.getCostPrice()))
                    .enableCount(new BigDecimal(holdsVo.getEnableAmount()))
                    .currentCount(new BigDecimal(holdsVo.getCurrentAmount()))
                    .exchangeType(holdsVo.getExchangeType())
                    .keepCostPrice(new BigDecimal(holdsVo.getKeepCostPrice()))
                    .marketValue(new BigDecimal(holdsVo.getMarketValue()))
                    .gapValue(new BigDecimal(holdsVo.getIncomeBalance()))
                    .build();
            return stockHoldingDTO;
        }).collect(Collectors.toList());
        return holdList;
    }

}
