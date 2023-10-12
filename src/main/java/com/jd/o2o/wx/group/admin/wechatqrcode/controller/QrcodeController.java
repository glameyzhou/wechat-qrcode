package com.jd.o2o.wx.group.admin.wechatqrcode.controller;

import com.google.common.collect.ImmutableMap;
import com.jd.o2o.wx.group.admin.wechatqrcode.constants.WecomAdminConstants;
import com.jd.o2o.wx.group.admin.wechatqrcode.domain.APIResponse;
import com.jd.o2o.wx.group.admin.wechatqrcode.domain.GroupChatListResponse;
import com.jd.o2o.wx.group.admin.wechatqrcode.exception.AccessIllegalException;
import com.jd.o2o.wx.group.admin.wechatqrcode.exception.InvalidHttpCookieException;
import com.jd.o2o.wx.group.admin.wechatqrcode.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

/**
 * @author zhouyang281
 * @date 2023-10-12
 */
@RestController
public class QrcodeController {


    private static final Logger LOGGER = LoggerFactory.getLogger(QrcodeController.class);

    @Resource
    private HeartbeatService heartbeatService;

    @Resource
    private LoginService loginService;
    @Resource
    private ContactService contactService;
    @Resource
    private GroupChatService groupChatService;
    @Resource
    private CookieRepository cookieRepository;

    private void checkKey(String key) {
        if (!StringUtils.equals(key, "R0ls1ZD8KzPgMuG9")) {
            throw new AccessIllegalException("[" + key + "]未授权禁止访问");
        }
    }

    @GetMapping({"/home", "", "/", "/index"})
    public APIResponse<String> home() {
        return APIResponse.success("wechat-qrcode");
    }

    @GetMapping("heartbeat")
    public APIResponse<String> heartbeat(@RequestParam("key") String key) {
        checkKey(key);
        // 处理两个异常 1、无cookie 2、扫码超时 这两个异常出现的时候，重新执行一次 @see LoginService.genCookie()接口。
        // 如果出现异常，等待下一次心跳执行即可。
        try {
            // 心跳
            heartbeatService.heartbeat();
            // 获取企微员工信息，并存储到缓存中
            contactService.storeContactMemberToLocalCache();
        } catch (Exception e) {
            if (e instanceof InvalidHttpCookieException) {
                try {
                    loginService.genCookie();
                } catch (Exception exception) {
                    LOGGER.error("获取Cookie异常", exception);
                }
            } else {
                LOGGER.error("执行企微后台心跳异常", e);
            }
        }
        String cookie = cookieRepository.load(WecomAdminConstants.Network.COOKIE_JDDJ);
        return APIResponse.success(cookie);
    }


    @GetMapping("getQrcodeUrl")
    public APIResponse<String> getQrcode(@RequestParam("key") String key, @RequestParam("userId") String userId, @RequestParam("groupChatName") String groupChatName) {
        checkKey(key);
        if (StringUtils.isAnyBlank(userId, groupChatName)) {
            return APIResponse.fail(404, "入参不能为空");
        }
        // owner -> vid
        String vid = contactService.getMemberVidByUserId(userId);
        if (StringUtils.isBlank(vid)) {
            LOGGER.warn("获取企微用户vid为空, userId={}, groupChatName={}", userId, groupChatName);
            return APIResponse.fail(404, "userId=[" + userId + "]未找到对应的VID");
        }

        // vid + name -> groupList
        List<GroupChatListResponse.GroupChat> rpcGroupChatList = groupChatService.getGroupChatList(vid, groupChatName);
        if (CollectionUtils.isEmpty(rpcGroupChatList)) {
            LOGGER.warn("获取企微社群集合为空, userId={}, groupChatName={}, vid={}, wxGroupChat={}", userId, groupChatName, vid);
            return APIResponse.fail(404, "userId=[" + userId + "], groupChatName=[" + groupChatName + "]未找到对应的社群");
        }

        GroupChatListResponse.GroupChat groupChat = rpcGroupChatList.stream().sorted(Comparator.comparingLong(GroupChatListResponse.GroupChat::getCreateTime)).findFirst().get();
        // roomId -> qrcode
        String roomId = groupChat.getRoomId();
        String qrcodeUrl = groupChatService.getQrcodeUrl(roomId);
        if (StringUtils.isBlank(qrcodeUrl)) {
            LOGGER.warn("获取企微社群二维码为空, userId={}, groupChatName={}, vid={}, roomId={}, ", userId, groupChatName, vid, roomId);
            return APIResponse.fail(404, "userId=[" + userId + "], groupChatName=[" + groupChatName + "] 未查询到社群二维码，可能社群开启了进群验证");
        }
        LOGGER.warn("获取企微社群二维码成功, userId={}, groupChatName={}, vid={}, roomId={}, qrcodeUrl={}", userId, groupChatName, vid, roomId, qrcodeUrl);
        ImmutableMap<String, String> map = ImmutableMap.of("userId", userId,
                "groupChatName", groupChatName,
                "adminName", groupChat.getAdminName(),
                "qrcodeUrl", qrcodeUrl);
        return APIResponse.success(map);
    }

    @GetMapping("storeCookie")
    public APIResponse<Boolean> storeCookie(@RequestParam("key") String key, @RequestParam("cookie") String cookie) {
        if (StringUtils.isAnyBlank(cookie)) {
            return APIResponse.fail(404, "参数为空");
        }
        cookieRepository.store(WecomAdminConstants.Network.COOKIE_JDDJ, cookie);
        return APIResponse.success(true);
    }
}
