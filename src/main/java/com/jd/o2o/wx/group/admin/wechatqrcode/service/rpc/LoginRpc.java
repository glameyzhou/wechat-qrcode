package com.jd.o2o.wx.group.admin.wechatqrcode.service.rpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Uninterruptibles;
import com.jd.o2o.wx.group.admin.wechatqrcode.constants.WecomAdminConstants;
import com.jd.o2o.wx.group.admin.wechatqrcode.constants.WecomAdminConstants.Header;
import com.jd.o2o.wx.group.admin.wechatqrcode.domain.QrcodeStatusResponse;
import com.jd.o2o.wx.group.admin.wechatqrcode.utils.RestUtils;
import org.apache.commons.lang3.StringUtils;
import org.htmlunit.*;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.util.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 企微后台-二维码登陆相关RPC
 *
 * @author zhouyang281
 * @date 2023-09-27
 */
@Component
public class LoginRpc {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginRpc.class);

    /**
     * 获取二维码key
     *
     * @return 二维码标识key
     */
    public String getQrcodeKey() {
        Map<String, Object> paramsMap = ImmutableMap.<String, Object>builder()
                                                    .put("r", System.currentTimeMillis())
                                                    .put("login_type", "login_admin")
                                                    .put("callback", "wwqrloginCallback_" + System.currentTimeMillis())
                                                    .put("redirect_uri", WecomAdminConstants.Domain.BASE_DOMAIN + "/wework_admin/loginpage_wx?_r=" + new Random().nextInt(1000) + "&from=myhome&url_hash=")
                                                    .put("crossorigin", "1")
                                                    .build();
        String params = paramsMap.entrySet().stream()
                                 .map(entry -> entry.getKey() + "=" + RestUtils.encode(entry.getValue().toString()))
                                 .collect(Collectors.joining("&"));
        String url = WecomAdminConstants.Domain.BASE_DOMAIN + "/wework_admin/wwqrlogin/mng/get_key?" + params;
        String response = RestUtils.get(url, Header.BASE_HEADER_MAP);

        //{"data":{"qrcode_key":"2e0972c9b425e095","pc_http_port":[],"pc_https_port":[]}}
        Optional<String> optional = Optional.ofNullable(JSON.parseObject(response))
                                            .map(x -> x.getJSONObject("data"))
                                            .map(x -> x.getString("qrcode_key"));
        return optional.orElse(null);
    }

    @Nonnull
    public String genQrcodeUrl(@Nonnull String qrcodeKey) {
        return String.format(WecomAdminConstants.Domain.QRCODE_URL_TEMPLATE, qrcodeKey);
    }

    /**
     * 登陆二维码2分钟有效期(实操的时候超时时间要小于2分钟)
     *
     * @param qrcodeKey 登陆二维码唯一标记
     * @return 扫码状态
     */
    public QrcodeStatusResponse.QrcodeStatus getQrcodeScanStatus(String qrcodeKey) {
        Map<String, Object> paramsMap = ImmutableMap.<String, Object>builder()
                                                    .put("r", System.currentTimeMillis())
                                                    .put("qrcode_key", qrcodeKey)
                                                    .put("status", "")
                                                    .build();
        String params = paramsMap.entrySet()
                                 .stream()
                                 .map(entry -> entry.getKey() + "=" + RestUtils.encode(entry.getValue().toString()))
                                 .collect(Collectors.joining("&"));

        String url = WecomAdminConstants.Domain.BASE_DOMAIN + "/wework_admin/wwqrlogin/check?" + params;
        QrcodeStatusResponse response = RestUtils.get(url, Header.BASE_HEADER_MAP, new TypeReference<QrcodeStatusResponse>() {
        });

        //{"data":{"status":"QRCODE_SCAN_NEVER","auth_source":"SOURCE_FROM_WEWORK","corp_id":0,"code_type":2,"clientip":"106.38.115.58","confirm_clientip":""}}
        //{"data":{"status":"QRCODE_SCAN_ING","auth_source":"SOURCE_FROM_WEWORK","corp_id":0,"code_type":2,"clientip":"115.47.145.240","confirm_clientip":"115.47.145.240"}}
        //{"data":{"status":"QRCODE_SCAN_SUCC","auth_code":"zlImb_a4PVmFmD7wHCODPMSlja6-FeZQPA1JbD1NIq4","auth_source":"SOURCE_FROM_WEWORK","corp_id":0,"code_type":2,"clientip":"106.38.115.21","confirm_clientip":"2408:8409:8450:8db9:71e6:1f81:1d0f:3b51"}}
        return Optional.ofNullable(response)
                       .map(QrcodeStatusResponse::getData)
                       .orElse(null);
    }

    public boolean isQrcodeScanSuccess(QrcodeStatusResponse.QrcodeStatus qrcodeStatus) {
        String status = Optional.ofNullable(qrcodeStatus)
                                .map(QrcodeStatusResponse.QrcodeStatus::getStatus)
                                .orElse(null);
        return StringUtils.equals(WecomAdminConstants.Network.QRCODE_SCAN_STATUS_SUCCESS, status);
    }


    public String getLoginCookie(String qrcodeKey, String authCode, String authSource) {
        String url = buildGetCookieUrl(qrcodeKey, authCode, authSource);
        return RestUtils.getCookieByRedirect(url, Header.BASE_HEADER_MAP);
    }


    public String getLoginCookieByHtmlUnit(String qrcodeKey, String authCode, String authSource) {
        String url = buildGetCookieUrl(qrcodeKey, authCode, authSource);

        try (WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.waitForBackgroundJavaScript(120000);
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.setJavaScriptTimeout(6000);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.setCssErrorHandler(new SilentCssErrorHandler());
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setTimeout(10000);
            webClient.getOptions().setRedirectEnabled(true);
            webClient.getCookieManager().setCookiesEnabled(true);

            // 请求业务
            HtmlPage htmlPage = webClient.getPage(url);

            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> getLoginCookieByHtmlUnitInner(webClient, htmlPage));
            return future.get(20, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("getCookieByHtmlUnit error", e);
        }
        return null;
    }

    private String getLoginCookieByHtmlUnitInner(WebClient webClient, HtmlPage htmlPage) {
        while (true) {
            LOGGER.info("PageContent, url={}, content={}", htmlPage.getUrl(), htmlPage.getTextContent());
            CookieManager cookieManager = webClient.getCookieManager();
            Cookie cookie = cookieManager.getCookie(Header.COOKIE_KEY_SID);
            if (cookie != null && StringUtils.isNotBlank(cookie.getValue())) {
                return cookie.toString();
            }
            Uninterruptibles.sleepUninterruptibly(2000,TimeUnit.MILLISECONDS);
        }
    }

    private static String buildGetCookieUrl(String qrcodeKey, String authCode, String authSource) {
        Map<String, Object> paramsMap = ImmutableMap.<String, Object>builder()
                                                    .put("_r", new Random().nextInt(1000))
                                                    .put("from", "myhome")
                                                    .put("url_hash", "")
                                                    .put("code", authCode)
                                                    .put("wwqrlogin", "1")
                                                    .put("qrcode_key", qrcodeKey)
                                                    .put("auth_source", authSource)
                                                    .build();
        String params = paramsMap.entrySet()
                                 .stream()
                                 .map(entry -> entry.getKey() + "=" + RestUtils.encode(entry.getValue().toString()))
                                 .collect(Collectors.joining("&"));
        String url = WecomAdminConstants.Domain.BASE_DOMAIN + "/wework_admin/loginpage_wx?" + params;
        return url;
    }
}
