package com.jd.o2o.wx.group.admin.wechatqrcode.constants;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author zhouyang281
 * @date 2023-09-2023/9/27
 */
public class WecomAdminConstants {

    public static final class Header {
        public static final String COOKIE = "Cookie";
        public static final String Location = "Location";
        public static final String SET_COOKIE = "Set-Cookie";
        public static final String USER_AGENT = "User-Agent";
        public static final String REFERER = "Referer";
        public static final String CONTENT_TYPE = "Content-Type";

        // public static final String
        public static final String CONTENT_TYPE_FORM_VALUE = "application/x-www-form-urlencoded";
        public static final String CONTENT_TYPE_JSON_VALUE = "application/json";

        public static final String COOKIE_KEY_SID = "wwrtx.sid";
        public static final Map<String, String> BASE_HEADER_MAP = ImmutableMap.<String, String>builder()
                                                                              .put(USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36")
                                                                              // .put("Sec-Ch-Ua", "\"Google Chrome\";v=\"117\", \"Not;A=Brand\";v=\"8\", \"Chromium\";v=\"117\"")
                                                                              // .put("Sec-Ch-Ua-Mobile", "?0")
                                                                              // .put("Sec-Ch-Ua-Platform", "\"macOS\"")
                                                                              .put("Sec-Fetch-Mode", "cors")
                                                                              .put("Sec-Fetch-Site", "same-origin")
                                                                              .put(":authority", "work.weixin.qq.com")
                                                                              .put(":scheme", "https")
                                                                              .put(REFERER, Domain.BASE_DOMAIN + "/wework_admin/loginpage_wx")
                                                                              .build();
    }

    public static final class Domain {
        public static final String BASE_DOMAIN = "https://work.weixin.qq.com";
        // 登陆成功后-首页
        public static final String DOMAIN_INDEX_PAGE = "https://work.weixin.qq.com/wework_admin/frame";
        public static final String QRCODE_URL_TEMPLATE = BASE_DOMAIN + "/wework_admin/wwqrlogin/mng/qrcode?qrcode_key=%s&login_type=login_admin";
    }

    public static final class Network {
        // 等待扫码
        public static final String QRCODE_SCAN_STATUS_NEVER = "QRCODE_SCAN_NEVER";
        // 扫码后，待确认
        public static final String QRCODE_SCAN_STATUS_ING = "QRCODE_SCAN_ING";
        // 扫码并确认
        public static final String QRCODE_SCAN_STATUS_SUCCESS = "QRCODE_SCAN_SUCC";
        // 扫码后取消
        public static final String QRCODE_SCAN_STATUS_FAIL = "QRCODE_SCAN_FAIL";

        public static final String COOKIE_JDDJ = "1970325088090171";

        public static final int HTTP_CODE_TO_MANY_REQUESTS = 429;
    }

    public static final class Tool {
        public static final Splitter SPLITTER_SEMICOLON = Splitter.on(";").trimResults();
        public static final Splitter SPLITTER_EQUALS = Splitter.on("=").trimResults();
        public static final Joiner JOINER_SEMICOLON = Joiner.on(";").skipNulls();

        /**
         * 线下推广关注的社群是从2023-06-01创建的
         */
        public static final long GROUP_CHAT_SCAN_START_TIME = 1685548800;
    }

    public static final class CacheKeyPrefix {
        // 企微后台cookie  key=corpId value=cookie
        public static final String WECOM_COOKIE = "wecom_cookie_";
        public static final String WECOM_CONTRACT_USERID_VID_ = "wecom_userId_vid_";
    }

    public static class ConfigKey {
        /**
         * 京me接收人列表:企微后台登陆二维码，数组形式
         */
        public static final String JINGME_RECEIVER_QRCODE_REFRESH_LOGIN = "jingme.receiver.qrcode.refresh.login";

        /**
         * 京me接收人列表:社群死码刷新结果，数组形式
         */
        public static final String JINGME_RECEIVER_QRCODE_REFRESH_RESULT = "jingme.receiver.qrcode.refresh.result";
    }

}
