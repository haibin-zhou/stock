package com.koder.stock.coreservice.thirdparty.hs.msghandler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.huasheng.quant.open.sdk.domain.ModelResult;
import com.huasheng.quant.open.sdk.protobuf.common.constant.NotifyMsgTypeProto;
import com.huasheng.quant.open.sdk.protobuf.common.msg.NotifyProto;
import com.huasheng.quant.open.sdk.protobuf.hq.common.dto.OrderBookProto;
import com.huasheng.quant.open.sdk.protobuf.hq.notify.OrderBookFullNotifyProto;
import com.koder.stock.client.dto.StockBasicDTO;
import com.koder.stock.client.dto.StockTradeOrderDTO;
import com.koder.stock.coreservice.config.GlobalConfig;
import com.koder.stock.coreservice.domain.constant.StockBasicMarket;
import com.koder.stock.coreservice.domain.constant.StockEntrustBS;
import com.koder.stock.coreservice.listenner.StockTradeOrderCreationMsgListener;
import com.koder.stock.coreservice.service.StockBasicInformationService;
import com.koder.stock.coreservice.service.StockTradeOrderService;
import com.koder.stock.coreservice.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class OrderBookNotifyMsgHandler implements NotificationMsgHandler {
    private Logger logger = LoggerFactory.getLogger(OrderBookNotifyMsgHandler.class);
    @Autowired
    private StockTradeOrderService tradeOrderService;
    @Autowired
    private GlobalConfig globalConfig;
    @Autowired
    private StockTradeOrderCreationMsgListener stockTradeOrderCreationMsgListener;
    @Autowired
    private StockBasicInformationService stockBasicInformationService;

    @Override
    public NotifyMsgTypeProto.NotifyMsgType getMsgType() {
        return NotifyMsgTypeProto.NotifyMsgType.OrderBookNotifyMsgType;
    }

    @Override
    public ModelResult<Boolean> handle(NotifyProto.PBNotify notify) throws InvalidProtocolBufferException {
        // 港股才处理：
        ModelResult modelResult = new ModelResult<>();
        modelResult.setModel(true);
        OrderBookFullNotifyProto.OrderBookFullNotify orderBookFullNotify =
                OrderBookFullNotifyProto.OrderBookFullNotify.parseFrom(notify.getPayload().getValue());

        if (orderBookFullNotify.getMktTmType() != 0) { // 非盘中行情
            return modelResult;
        }

        if (Calendar.getInstance().before(DateUtil.getHKOpenTime())) {
            logger.warn("非盘中行情时间，退出");
            return modelResult;
        }

        StockBasicDTO stockBasicDTO = stockBasicInformationService.queryByCode(orderBookFullNotify.getSecurity().getCode());
        if (stockBasicDTO == null) {
            return modelResult;
        }
        // 非港股行情
        if (stockBasicDTO.getMarket() != StockBasicMarket.HK_MARKET) {
            return modelResult;
        }

        String entrustBs = orderBookFullNotify.getSide() == 0 ? StockEntrustBS.BUY_IN : StockEntrustBS.SELL_OUT; // 买或者卖
        List<StockTradeOrderDTO> stockTradeOrders = tradeOrderService.queryUnEntrustedOrders(globalConfig.getSystemDefaultUserId(),
                orderBookFullNotify.getSecurity().getCode(), entrustBs);
        List<OrderBookProto.OrderBook> orderBooks = orderBookFullNotify.getOrderBookListList();
        Optional<OrderBookProto.OrderBook> maxPrice = orderBooks.stream().max(Comparator.comparing(orderBook -> new BigDecimal(orderBook.getPrice())));
        Optional<OrderBookProto.OrderBook> minPrice = orderBooks.stream().min(Comparator.comparing(orderBook -> new BigDecimal(orderBook.getPrice())));
        if (!maxPrice.isPresent() || !minPrice.isPresent()) {
            modelResult.setModel(true);
            return modelResult;
        }

        stockTradeOrders.stream().forEach(stockTradeOrderDTO -> {
            if (checkWhetherDoCreation(new BigDecimal(minPrice.get().getPrice()), new BigDecimal(maxPrice.get().getPrice()), stockTradeOrderDTO)) {
                stockTradeOrderCreationMsgListener.doOrderCreationMsg(stockTradeOrderDTO);
            }
        });

        modelResult.setModel(true);
        return modelResult;
    }

    public boolean checkWhetherDoCreation(BigDecimal minPrice, BigDecimal maxPrice, StockTradeOrderDTO stockTradeOrderDTO) {

        BigDecimal midPrice = maxPrice.add(minPrice).divide(new BigDecimal(2));

        if (stockTradeOrderDTO.getEntrustBs().equals(StockEntrustBS.BUY_IN)) {
            if (minPrice.compareTo(stockTradeOrderDTO.getEntrustPrice()) <= 0 && midPrice.compareTo(stockTradeOrderDTO.getEntrustPrice()) >= 0) {
                return true;
            }
        }

        if (stockTradeOrderDTO.getEntrustBs().equals(StockEntrustBS.SELL_OUT)) {
            if (maxPrice.compareTo(stockTradeOrderDTO.getEntrustPrice()) >= 0
                    && midPrice.compareTo(stockTradeOrderDTO.getEntrustPrice()) <= 0) {
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args) {
        OrderBookNotifyMsgHandler notifyMsgHandler = new OrderBookNotifyMsgHandler();
        StockTradeOrderDTO stockTradeOrderDTO = StockTradeOrderDTO.builder()
                .entrustPrice(new BigDecimal("85.35"))
                .entrustBs(StockEntrustBS.BUY_IN)
                .build();
        boolean result = notifyMsgHandler.checkWhetherDoCreation
                (new BigDecimal("85.35"), new BigDecimal("86.10"), stockTradeOrderDTO);
        System.out.println(result);

        Calendar openTime = Calendar.getInstance();

        openTime.set(Calendar.HOUR_OF_DAY,9);
        openTime.set(Calendar.MINUTE,30);
        System.out.println(openTime.get(Calendar.MINUTE)+":"+openTime.get(Calendar.HOUR_OF_DAY));
        System.out.println(openTime.before(Calendar.getInstance()));

    }

}
