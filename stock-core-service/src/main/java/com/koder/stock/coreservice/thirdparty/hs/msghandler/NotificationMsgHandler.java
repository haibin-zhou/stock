package com.koder.stock.coreservice.thirdparty.hs.msghandler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.huasheng.quant.open.sdk.domain.ModelResult;
import com.huasheng.quant.open.sdk.protobuf.common.constant.NotifyMsgTypeProto;
import com.huasheng.quant.open.sdk.protobuf.common.msg.NotifyProto;

public interface NotificationMsgHandler {

    NotifyMsgTypeProto.NotifyMsgType getMsgType();
    ModelResult<Boolean> handle(NotifyProto.PBNotify notify) throws InvalidProtocolBufferException;

}
