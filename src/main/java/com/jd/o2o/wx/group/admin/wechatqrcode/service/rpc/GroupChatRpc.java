package com.jd.o2o.wx.group.admin.wechatqrcode.service.rpc;

import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jd.o2o.wx.group.admin.wechatqrcode.domain.GroupChatListResponse;
import com.jd.o2o.wx.group.admin.wechatqrcode.domain.GroupChatQrcodeResponse;
import com.jd.o2o.wx.group.admin.wechatqrcode.service.CookieRepository;
import com.jd.o2o.wx.group.admin.wechatqrcode.constants.WecomAdminConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.jd.o2o.wx.group.admin.wechatqrcode.utils.RestUtils.encode;
import static com.jd.o2o.wx.group.admin.wechatqrcode.utils.RestUtils.get;

/**
 * 企微后台-社群相关RPC
 */
@Component
public class GroupChatRpc {

    @Resource
    private CookieRepository CookieRepository;

    // 检索
    // https://work.weixin.qq.com/wework_admin/customer/getGroupChatList?lang=zh_CN&f=json&ajax=1&timeZoneInfo%5Bzone_offset%5D=-8&random=0.40352690933387203&
    // off_set=0&limit=10&create_ts_begin=1569340800&create_ts_end=1695807496&page=1&accurate_keywords%5B%5D=%E4%BA%AC%E4%B8%9C%E5%88%B0%E5%AE%B6%E7%A6%8F%E5%88%A9%E7%BE%A4&last_page_max_id=0&vids%5B%5D=1688855127466196

    /**
     * 通过关键字查询社群信息
     *
     * @param offset           起始位置     默认 0
     * @param limit            每次查询条数 默认10
     * @param vids             企微员工ID
     * @param accurateKeywords 社群名称，精确关键字查询。
     * @return 社群列表
     */
    public GroupChatListResponse.GroupChatListData getGroupChatList(long offset, int limit, @Nullable List<String> vids,
                                                                    @Nullable List<String> accurateKeywords, @Nullable String keywords) {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
                                                                   .put("lang", "zh_CN")
                                                                   .put("f", "json")
                                                                   .put("ajax", "1")
                                                                   .put("timeZoneInfo[zone_offset]", "-8")
                                                                   .put("random", System.currentTimeMillis())
                                                                   .put("off_set", offset)
                                                                   .put("limit", limit)
                                                                   .put("create_ts_begin", 1569340800) // 2019-09-25 00:00:00
                                                                   .put("create_ts_end", (System.currentTimeMillis() / 1000));
        if (CollectionUtils.isNotEmpty(vids)) {
            vids.stream().filter(StringUtils::isNotBlank).map(StringUtils::trim).forEach(x -> builder.put("vids[]", x));
        }
        if (CollectionUtils.isNotEmpty(accurateKeywords)) {
            accurateKeywords.stream().filter(StringUtils::isNotBlank).map(StringUtils::trim).forEach(x -> builder.put("accurate_keywords[]", x));
        }
        if (StringUtils.isNotBlank(keywords)) {
            builder.put("keywords", keywords);
        }
        String params = builder.build().entrySet()
                               .stream()
                               .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue().toString()))
                               .collect(Collectors.joining("&"));
        Map<String, String> header = Maps.newHashMap(WecomAdminConstants.Header.BASE_HEADER_MAP);
        header.put(WecomAdminConstants.Header.COOKIE, CookieRepository.load(WecomAdminConstants.Network.COOKIE_JDDJ));
        header.put(WecomAdminConstants.Header.CONTENT_TYPE, WecomAdminConstants.Header.CONTENT_TYPE_FORM_VALUE);
        header.put(WecomAdminConstants.Header.REFERER, WecomAdminConstants.Domain.DOMAIN_INDEX_PAGE);
        String url = WecomAdminConstants.Domain.BASE_DOMAIN + "/wework_admin/customer/getGroupChatList?" + params;
        GroupChatListResponse response = get(url, header, new TypeReference<GroupChatListResponse>() {
        });
        return Optional.ofNullable(response)
                       .map(GroupChatListResponse::getData)
                       .orElse(null);
    }


    public String getGroupChatQrcodeUrl(String roomId) {
        Map<String, Object> paramsMap = ImmutableMap.<String, Object>builder()
                                                    .put("lang", "zh_CN")
                                                    .put("f", "json")
                                                    .put("ajax", "1")
                                                    .put("timeZoneInfo[zone_offset]", "-8")
                                                    .put("random", System.currentTimeMillis())
                                                    .put("off_set", "0")
                                                    .put("limit", "600")
                                                    .put("roomid", roomId)
                                                    .put("page", "1")
                                                    .build();
        String params = paramsMap.entrySet()
                                 .stream()
                                 .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue().toString()))
                                 .collect(Collectors.joining("&"));
        Map<String, String> header = Maps.newHashMap(WecomAdminConstants.Header.BASE_HEADER_MAP);
        header.put(WecomAdminConstants.Header.COOKIE, CookieRepository.load(WecomAdminConstants.Network.COOKIE_JDDJ));
        header.put(WecomAdminConstants.Header.CONTENT_TYPE, WecomAdminConstants.Header.CONTENT_TYPE_FORM_VALUE);
        header.put(WecomAdminConstants.Header.REFERER, WecomAdminConstants.Domain.DOMAIN_INDEX_PAGE);
        String url = WecomAdminConstants.Domain.BASE_DOMAIN + "/wework_admin/customer/qun/getRoomMemberList?" + params;
        GroupChatQrcodeResponse response = get(url, header, new TypeReference<GroupChatQrcodeResponse>() {
        });
        return Optional.ofNullable(response)
                       .map(GroupChatQrcodeResponse::getData)
                       .map(GroupChatQrcodeResponse.GroupChatQrcode::getQrcodeUrl).orElse(null);
    }

}
