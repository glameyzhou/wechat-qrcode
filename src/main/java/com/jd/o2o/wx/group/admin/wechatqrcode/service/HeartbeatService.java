package com.jd.o2o.wx.group.admin.wechatqrcode.service;

import com.jd.o2o.wx.group.admin.wechatqrcode.service.rpc.HeartbeatRpc;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhouyang281
 * @date 2023-09-27
 */
@Component
public class HeartbeatService {

    @Resource
    private HeartbeatRpc heartbeatRpc;

    public boolean heartbeat() {
        return heartbeatRpc.heartbeat();
    }
}
