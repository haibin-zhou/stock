package com.koder.stock.coreservice.thirdparty.hs.msghandler;

import com.huasheng.quant.open.sdk.domain.ModelResult;
import com.huasheng.quant.open.sdk.protobuf.common.constant.NotifyMsgTypeProto;
import com.huasheng.quant.open.sdk.protobuf.common.msg.NotifyProto;
import com.huasheng.quant.open.sdk.protobuf.trade.notify.TradeStockDeliverNotifyProto;
import com.koder.stock.client.dto.StockTradeOrderDTO;
import com.koder.stock.coreservice.domain.constant.StockTradeOrderFeatureKeys;
import com.koder.stock.coreservice.service.StockTradeOrderService;
import com.koder.stock.coreservice.thirdparty.hs.HSCommunicationKeys;
import com.koder.stock.coreservice.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class TradeDeliveryMsgHandler implements NotificationMsgHandler {

    private Logger logger = LoggerFactory.getLogger(TradeDeliveryMsgHandler.class);
    @Autowired
    private StockTradeOrderService stockTradeOrderService;
    @Autowired
    private HSCommunicationKeys hsCommunicationKeys;

    public static Map<String, String> statusMapping = new HashMap<>();

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
     * #
     */
    @Override
    public NotifyMsgTypeProto.NotifyMsgType getMsgType() {
        return NotifyMsgTypeProto.NotifyMsgType.TradeStockDeliverMsgType;
    }

    @Override
    public ModelResult<Boolean> handle(NotifyProto.PBNotify notify) {
        if (!hsCommunicationKeys.getInitTradeApi()) {
            return new ModelResult<Boolean>().withModel(true);
        }
        try {
            TradeStockDeliverNotifyProto.TradeStockDeliverNotify tradeStockDeliverNotify =
                    TradeStockDeliverNotifyProto.TradeStockDeliverNotify.parseFrom(notify.getPayload().getValue());
            logger.info("Trade push record no: {}, entrustStatus : {}, entrustNo: {}, remark: {} , deliveryDate {} , DeliveryTime{}",
                    tradeStockDeliverNotify.getRecordNo(), tradeStockDeliverNotify.getEntrustStatus(),
                    tradeStockDeliverNotify.getEntrustNo(), tradeStockDeliverNotify.getRemark(),
                    tradeStockDeliverNotify.getBusinessDate(), tradeStockDeliverNotify.getBusinessTime());

            StockTradeOrderDTO stockTradeOrderDTO = StockTradeOrderDTO.builder()
                    .outerTradeNo(tradeStockDeliverNotify.getEntrustNo())
                    .stockCode(tradeStockDeliverNotify.getStockCode())
                    .outerTradeNo(tradeStockDeliverNotify.getRecordNo())
                    .status(tradeStockDeliverNotify.getEntrustStatus())
                    .dealPrice(new BigDecimal(tradeStockDeliverNotify.getBusinessPrice()))
                    .dealCount(Integer.valueOf(tradeStockDeliverNotify.getBusinessAmount()))
                    .build();
            String dateTime = tradeStockDeliverNotify.getBusinessDate() + tradeStockDeliverNotify.getBusinessTime();
            Instant instant = DateUtil.getFromString(dateTime, DateUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
            stockTradeOrderDTO.setDealDateTime(instant.toEpochMilli());
            stockTradeOrderDTO.putIfAbsent(StockTradeOrderFeatureKeys.REMARK, tradeStockDeliverNotify.getRemark());
            stockTradeOrderService.entrustCallback(stockTradeOrderDTO);
        } catch (Exception e) {
            logger.warn("trade delivery msg handle error:[{}]", notify.getNotifyId(), e);
            return new ModelResult<Boolean>().withModel(false);
        }
        return new ModelResult().withModel(true);
    }
}
