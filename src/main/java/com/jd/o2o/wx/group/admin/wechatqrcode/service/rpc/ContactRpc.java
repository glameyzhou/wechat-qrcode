package com.jd.o2o.wx.group.admin.wechatqrcode.service.rpc;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jd.o2o.wx.group.admin.wechatqrcode.domain.ContractMemberResponse;
import com.jd.o2o.wx.group.admin.wechatqrcode.utils.RestUtils;
import com.jd.o2o.wx.group.admin.wechatqrcode.constants.WecomAdminConstants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 企微后台-内部联系人相关RPC
 */
@Component
public class ContactRpc {

    @Resource
    private com.jd.o2o.wx.group.admin.wechatqrcode.service.CookieRepository CookieRepository;

    /**
     * 内部员工列表
     */
    public ContractMemberResponse.ContractMemberData getContractMemberList() {
        // https://work.weixin.qq.com/wework_admin/contacts/member/cache?lang=zh_CN&f=json&ajax=1&timeZoneInfo%5Bzone_offset%5D=-8&random=0.2885879949991548
        String queryParams = ImmutableMap.<String, Object>builder()
                                         .put("lang", "zh_CN")
                                         .put("f", "json")
                                         .put("ajax", "1")
                                         .put("timeZoneInfo[zone_offset]", "-8")
                                         .put("random", System.currentTimeMillis())
                                         .build()
                                         .entrySet()
                                         .stream()
                                         .map(entry -> RestUtils.encode(entry.getKey()) + "=" + RestUtils.encode(entry.getValue().toString()))
                                         .collect(Collectors.joining("&"));
        Map<String, String> header = Maps.newHashMap(WecomAdminConstants.Header.BASE_HEADER_MAP);
        header.put(WecomAdminConstants.Header.COOKIE, CookieRepository.load(WecomAdminConstants.Network.COOKIE_JDDJ));
        header.put(WecomAdminConstants.Header.CONTENT_TYPE, WecomAdminConstants.Header.CONTENT_TYPE_FORM_VALUE);
        header.put(WecomAdminConstants.Header.REFERER, WecomAdminConstants.Domain.DOMAIN_INDEX_PAGE);
        String url = WecomAdminConstants.Domain.BASE_DOMAIN + "/wework_admin/contacts/member/cache?" + queryParams;
        String response = RestUtils.postForm(url, header, null);
        ContractMemberResponse contractMemberResponse = JSON.parseObject(response, ContractMemberResponse.class);
        return Optional.ofNullable(contractMemberResponse)
                       .map(ContractMemberResponse::getData)
                       .orElse(null);
    }

}
