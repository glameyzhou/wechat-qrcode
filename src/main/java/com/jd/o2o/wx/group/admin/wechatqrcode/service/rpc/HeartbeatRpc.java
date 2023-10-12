package com.jd.o2o.wx.group.admin.wechatqrcode.service.rpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.jd.o2o.wx.group.admin.wechatqrcode.constants.WecomAdminConstants;
import com.jd.o2o.wx.group.admin.wechatqrcode.service.CookieRepository;
import com.jd.o2o.wx.group.admin.wechatqrcode.utils.RestUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

import static com.jd.o2o.wx.group.admin.wechatqrcode.constants.WecomAdminConstants.Domain.BASE_DOMAIN;
import static com.jd.o2o.wx.group.admin.wechatqrcode.constants.WecomAdminConstants.Domain.DOMAIN_INDEX_PAGE;
import static com.jd.o2o.wx.group.admin.wechatqrcode.constants.WecomAdminConstants.Header.*;

/**
 * 企微后台-心跳相关，随便找了一个字节数最少的API
 *
 * @author zhouyang281
 * @date 2023-09-27
 */
@Component
public class HeartbeatRpc {

    @Resource
    private CookieRepository cookieRepository;

    /**
     * 访问地址 https://work.weixin.qq.com/wework_admin/third/hasServiceCorp
     * 响应数据 {"data":{"hasServiceCorp":false}}
     */
    public boolean heartbeat() {
        Map<String, String> header = Maps.newHashMap(BASE_HEADER_MAP);
        header.put(COOKIE, cookieRepository.load(WecomAdminConstants.Network.COOKIE_JDDJ));
        header.put(REFERER, DOMAIN_INDEX_PAGE);
        String url = BASE_DOMAIN + "/wework_admin/third/hasServiceCorp";
        String response = RestUtils.get(url, header);
        JSONObject jsonObject = Optional.ofNullable(JSON.parseObject(response))
                                        .map(x -> x.getJSONObject("data"))
                                        .orElse(new JSONObject());
        return jsonObject.containsKey("hasServiceCorp");
    }
}
