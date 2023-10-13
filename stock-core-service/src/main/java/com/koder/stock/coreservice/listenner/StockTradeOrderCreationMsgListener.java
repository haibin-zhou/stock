package com.koder.stock.coreservice.listenner;

import com.koder.stock.client.dto.ResultDTO;
import com.koder.stock.client.dto.StockTradeOrderDTO;
import com.koder.stock.coreservice.domain.constant.StockEntrustStatus;
import com.koder.stock.coreservice.manager.StockTradeOrderManager;
import com.koder.stock.coreservice.service.StockTradeOrderService;
import com.koder.stock.coreservice.thirdparty.hs.HsApiProxy;
import com.koder.stock.coreservice.util.LockUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.koder.stock.coreservice.domain.constant.StockEntrustStatus.EN_TRUSTED_INVALID_ORDER;

@Component
public class StockTradeOrderCreationMsgListener {

    @Autowired
    private HsApiProxy hsApiProxy;
    @Autowired
    private StockTradeOrderManager stockTradeOrderManager;
    @Autowired
    private StockTradeOrderService stockTradeOrderService;

    private Map<String, Boolean> sentFail = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(StockTradeOrderCreationMsgListener.class);

    public void orderCreationMsg(StockTradeOrderDTO stockTradeOrderDTO) {
        // 如果是港股，暂时不委托
        if (StringUtils.isEmpty(stockTradeOrderDTO.getExchange())) {
            logger.warn("港股订单，不委托：[{}],[{}]", stockTradeOrderDTO.getStockCode(), stockTradeOrderDTO.getTradeOrderNo());
            return;
        }
        doOrderCreationMsg(stockTradeOrderDTO);
    }

    public void doOrderCreationMsg(StockTradeOrderDTO stockTradeOrderDTO) {
        boolean lock = LockUtil.getLock(LockUtil.EN_TRUSTED_ORDER, stockTradeOrderDTO.getTradeOrderNo());
        if (!lock) {
            logger.info("委托并发控制操作[{}],[{}]", stockTradeOrderDTO.getStockCode(), stockTradeOrderDTO.getTradeOrderNo());
            return;
        }
        logger.info("开始进行委托请求[{}],[{}]", stockTradeOrderDTO.getStockCode(), stockTradeOrderDTO.getTradeOrderNo());

        try {
            stockTradeOrderDTO = stockTradeOrderManager.queryOrderByTradeNo(stockTradeOrderDTO.getTradeOrderNo());
            if (!stockTradeOrderDTO.getStatus().equals(StockEntrustStatus.INIT_ED)) {
                return;
            }
            stockTradeOrderDTO.setStatus(StockEntrustStatus.EN_TRUSTING);
            stockTradeOrderService.entrustStatusUpdate(stockTradeOrderDTO);
            ResultDTO<StockTradeOrderDTO> resultDTO = hsApiProxy.entrustOrder(stockTradeOrderDTO);
            StockTradeOrderDTO retDTO = resultDTO.getResult();
            stockTradeOrderDTO.setOuterTradeNo(retDTO.getOuterTradeNo());
            stockTradeOrderDTO.setStatus(retDTO.getStatus());
            stockTradeOrderService.entrustStatusUpdate(stockTradeOrderDTO);
        } catch (Exception e) {
            logger.warn("trade order entrust error, trade order no[{}]", stockTradeOrderDTO.getTradeOrderNo(), e);
        } finally {
            LockUtil.releaseLock(LockUtil.EN_TRUSTED_ORDER, stockTradeOrderDTO.getTradeOrderNo(),true);
        }
    }
}
