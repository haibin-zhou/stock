package com.koder.stock.coreservice.thirdparty.hs.msghandler;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huasheng.quant.open.sdk.domain.ModelResult;
import com.huasheng.quant.open.sdk.protobuf.common.constant.NotifyMsgTypeProto;
import com.huasheng.quant.open.sdk.protobuf.common.msg.NotifyProto;
import com.huasheng.quant.open.sdk.protobuf.hq.notify.BasicQotNotifyProto;
import com.koder.stock.client.dto.StockQuotationDTO;
import com.koder.stock.client.dto.StockTradeOrderDTO;
import com.koder.stock.coreservice.service.StockTradeOrderService;
import com.koder.stock.coreservice.util.LockUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class QuotationMsgHandler implements NotificationMsgHandler {

    private Logger logger = LoggerFactory.getLogger(QuotationMsgHandler.class);

    @Autowired
    private StockTradeOrderService stockTradeOrderService;

    @Override
    public NotifyMsgTypeProto.NotifyMsgType getMsgType() {
        return NotifyMsgTypeProto.NotifyMsgType.BasicQotNotifyMsgType;
    }

    @Override
    public ModelResult<Boolean> handle(NotifyProto.PBNotify notify) {
        ModelResult<Boolean> result = new ModelResult<>();
        logger.warn("handle msg notifyId[{}] start", notify.getNotifyId());
        StockQuotationDTO quotationDTO = null;
        try {
            BasicQotNotifyProto.BasicQotNotify basicQotNotify = BasicQotNotifyProto.BasicQotNotify.parseFrom(notify.getPayload().getValue());
            quotationDTO = StockQuotationDTO.builder()
                    .lastClosingPrice(new BigDecimal(basicQotNotify.getBasicQot().getLastClosePrice()).setScale(3, RoundingMode.HALF_UP))
                    .highestPrice(new BigDecimal(basicQotNotify.getBasicQot().getHighPrice()).setScale(3, RoundingMode.HALF_UP))
                    .latestPrice(new BigDecimal(basicQotNotify.getBasicQot().getLastPrice()).setScale(3, RoundingMode.HALF_UP))
                    .openingPrice(new BigDecimal(basicQotNotify.getBasicQot().getOpenPrice()).setScale(3, RoundingMode.HALF_UP))
                    .lowestPrice(new BigDecimal(basicQotNotify.getBasicQot().getLowPrice()).setScale(3, RoundingMode.HALF_UP))
                    .code(basicQotNotify.getBasicQot().getSecurity().getCode())
                    .build();
            logger.warn("data convertor notifyId[{}], result[{}]", notify.getNotifyId(), JSON.toJSONString(quotationDTO));
            LockUtil.getLock(LockUtil.QUOTATION_LISTEN, getLockKey(quotationDTO));
            List<StockTradeOrderDTO> orders = stockTradeOrderService.createTradeOrders(quotationDTO);
            if (CollectionUtils.isEmpty(orders)) {
                logger.warn("Not create any order base on notifyId[{}]", notify.getNotifyId());
            } else {
                orders.stream().forEach(order -> {
                    logger.warn("create order code:[{}],price[{}],volume[{}]", order.getStockCode(), order.getEntrustPrice().toPlainString(), order.getEntrustVolume());
                });
            }
        } catch (InvalidProtocolBufferException e) {
            logger.warn("handle msg notifyId[{}] error", notify.getNotifyId(), e);
            return result.withError(e.getMessage(), e.getMessage());
        } finally {
            if (quotationDTO != null) {
                LockUtil.releaseLock(LockUtil.QUOTATION_LISTEN, getLockKey(quotationDTO), true);
            }
        }
        return result.withModel(true);
    }

    private String getLockKey(StockQuotationDTO quotationDTO) {
        return StringUtils.join(quotationDTO.getCode(), quotationDTO.getLatestPrice().toPlainString());
    }

}
