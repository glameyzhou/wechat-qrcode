package com.jd.o2o.wx.group.admin.wechatqrcode.service;

import com.google.common.collect.Lists;
import com.jd.o2o.wx.group.admin.wechatqrcode.domain.JingMeSendNoticeRequest;
import com.jd.o2o.wx.group.admin.wechatqrcode.service.rpc.JingMeRpc;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhouyang281
 * @date 2023-09-27
 */
@Component
public class JingMeService {

    @Resource
    private JingMeRpc jingMeRpc;

    public boolean sendLoginQrcodeNotice(@Nonnull List<String> tos, @Nonnull String loginQrcodeUrl) {
        JingMeSendNoticeRequest request = new JingMeSendNoticeRequest();
        request.setTitle("企微后台登陆-二维码扫描提醒");
        request.setContent("\n请使用<企业微信>扫码，二分钟内有效。");
        request.setTos(Lists.newArrayList(tos));

        JingMeSendNoticeRequest.Extend extend = new JingMeSendNoticeRequest.Extend();
        extend.setPic(loginQrcodeUrl);
        extend.setUrl(loginQrcodeUrl);
        extend.setBtnNameCn("查看二维码");
        extend.setBtnNameEn("showQrcode");
        request.setExtend(extend);

        // return jingMeRpc.sendMessage(request);
        return true;
    }

    public boolean sendRefreshQrcodeUrlResult(@Nonnull List<String> tos, @Nonnull String content, @Nonnull String jumpUrl) {
        JingMeSendNoticeRequest request = new JingMeSendNoticeRequest();
        request.setTitle("企微社群-死码刷新结果明细");
        request.setContent(content);
        request.setTos(Lists.newArrayList(tos));

        JingMeSendNoticeRequest.Extend extend = new JingMeSendNoticeRequest.Extend();
        extend.setUrl(jumpUrl);
        extend.setBtnNameCn("查看异常明细");
        extend.setBtnNameEn("showFailRefreshResult");
        request.setExtend(extend);

        return jingMeRpc.sendMessage(request);
    }
}
