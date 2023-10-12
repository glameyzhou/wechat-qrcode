package com.jd.o2o.wx.group.admin.wechatqrcode.service.rpc;

import com.jd.o2o.wx.group.admin.wechatqrcode.domain.JingMeSendNoticeRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * 京ME消息发送
 *
 * @author zhouyang281
 * @date 2023-09-27
 */
@Component
public class JingMeRpc {


    public boolean sendMessage(@Nonnull JingMeSendNoticeRequest request) {
        // 获取accessToken
        return true;
    }
}
