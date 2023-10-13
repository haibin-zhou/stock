package com.koder.stock.coreservice.thirdparty.hs;

import com.huasheng.quant.open.api.PushMessageNotifyHandle;
import com.huasheng.quant.open.sdk.domain.ModelResult;
import com.huasheng.quant.open.sdk.protobuf.common.constant.NotifyMsgTypeProto;
import com.huasheng.quant.open.sdk.protobuf.common.msg.NotifyProto;
import com.huasheng.quant.open.sdk.protocol.ProtoBufNofityMessage;
import com.koder.stock.coreservice.thirdparty.hs.msghandler.NotificationMsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class HsMessageNotifyHandle extends PushMessageNotifyHandle {

    private static final Logger logger = LoggerFactory.getLogger(PushMessageNotifyHandle.class);
    private volatile boolean shutdown = false;
    private static int queueSize = 1000;
    private static long waitTime = 200;
    private BlockingQueue<ProtoBufNofityMessage> blockingQueue = new ArrayBlockingQueue(queueSize);
    private Map<NotifyMsgTypeProto.NotifyMsgType, NotificationMsgHandler> msgHandlerMap;

    @Autowired
    public void setMsgHandlerList(List<NotificationMsgHandler> handlerList) {
        if (CollectionUtils.isEmpty(handlerList)) {
            return;
        }
        msgHandlerMap = new HashMap<>();
        for (NotificationMsgHandler handler : handlerList) {
            msgHandlerMap.put(handler.getMsgType(), handler);
        }
    }

    public HsMessageNotifyHandle() {
        this(queueSize, waitTime);
    }

    public HsMessageNotifyHandle(int queueSize, long waitTime) {
        super(queueSize, waitTime);
    }

    public void push(ProtoBufNofityMessage protoBufNofityMessage) {
        try {
            this.blockingQueue.offer(protoBufNofityMessage, this.waitTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException var3) {
            logger.error("Push protoBufNotifyMessage to queue error.", var3);
        }
    }

    public void run() {
        while (!this.shutdown) {
            try {
                ProtoBufNofityMessage notifyMessage = (ProtoBufNofityMessage) this.blockingQueue.take();
                NotifyProto.PBNotify pbNotify = (NotifyProto.PBNotify) notifyMessage.getMsgBody();
                logger.info("****** Consume protoBufNotifyMessage ****** notifyType: {}, notifyId: {}, payLoad: {}", new Object[]{pbNotify.getNotifyMsgType(), pbNotify.getNotifyId(), pbNotify.getPayload()});
                NotificationMsgHandler handler = msgHandlerMap.get(pbNotify.getNotifyMsgType());
                if (handler == null) {
                    logger.warn("****** Consume protoBufNotifyMessage ****** no handler for this msgType: {}, notifyId: {}, payLoad: {}", new Object[]{pbNotify.getNotifyMsgType(), pbNotify.getNotifyId(), pbNotify.getPayload()});
                    continue;
                }
                ModelResult result = handler.handle(pbNotify);
                if (!result.isSuccess()) {
                    logger.error("****** Consume protoBufNotifyMessage ****** Msg handle failed, msgType: {}, notifyId: {}, payLoad: {}", new Object[]{pbNotify.getNotifyMsgType(), pbNotify.getNotifyId(), pbNotify.getPayload()});
                    continue;
                }
            } catch (Exception var16) {
                logger.error("Consumer protoBufNotifyMessage error.", var16);
            }
        }

    }

    public void shutdown() {
        this.shutdown = true;
    }
}
