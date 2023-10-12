package com.jd.o2o.wx.group.admin.wechatqrcode.service;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;
import com.jd.o2o.wx.group.admin.wechatqrcode.constants.WecomAdminConstants;
import com.jd.o2o.wx.group.admin.wechatqrcode.domain.QrcodeStatusResponse;
import com.jd.o2o.wx.group.admin.wechatqrcode.exception.BusinessException;
import com.jd.o2o.wx.group.admin.wechatqrcode.exception.QrcodeScanTimeoutException;
import com.jd.o2o.wx.group.admin.wechatqrcode.service.rpc.LoginRpc;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author zhouyang281
 * @date 2023-09-27
 */
@Component
public class LoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    @Resource
    private LoginRpc loginRpc;
    @Resource
    private JingMeService jingMeService;
    @Resource
    private CookieRepository cookieRepository;


    /**
     * 企微后台cookie生成
     * 调用方需要捕获二维码扫描异常 {@link QrcodeScanTimeoutException}
     */
    public void genCookie() throws QrcodeScanTimeoutException {
        String qrcodeKey = loginRpc.getQrcodeKey();
        if (StringUtils.isBlank(qrcodeKey)) {
            LOGGER.error("生成登陆二维码key失败");
            return;
        }

        String qrcodeUrl = loginRpc.genQrcodeUrl(qrcodeKey);
        LOGGER.info("qrcode url is {}", qrcodeUrl);
        // List<String> tos = configService.getList(ConfigKey.JINGME_RECEIVER_QRCODE_REFRESH_LOGIN, String.class, Lists.newArrayList("zhouyang281"));
        List<String> tos = Lists.newArrayList("zhouyang281");
        boolean sendNotice = jingMeService.sendLoginQrcodeNotice(tos, qrcodeUrl);
        if (!sendNotice) {
            LOGGER.error("发送京ME消息失败, tos={}, qrcodeUrl={}", tos, qrcodeUrl);
            return;
        }

        QrcodeStatusResponse.QrcodeStatus qrcodeStatus = getQrcodeScanStatusWithTimeout(qrcodeKey);
        if (!loginRpc.isQrcodeScanSuccess(qrcodeStatus)) {
            LOGGER.error("检测登陆二维码扫描状态失败, qrcodeStatus={}", qrcodeStatus);
            return;
        }

        String authCode = qrcodeStatus.getAuthCode();
        String authSource = qrcodeStatus.getAuthSource();
        String loginCookie = loginRpc.getLoginCookie(qrcodeKey, authCode, authSource);
        if (StringUtils.isBlank(loginCookie)) {
            LOGGER.error("获取cookie失败, qrcodeKey={}, authCode={}, authSource={}", qrcodeKey, authCode, authSource);
            return;
        }
        cookieRepository.store(WecomAdminConstants.Network.COOKIE_JDDJ, loginCookie);
        LOGGER.info("获取cookie成功, qrcodeUrl={}, cookie={}", qrcodeUrl, loginCookie);
    }

    private QrcodeStatusResponse.QrcodeStatus getQrcodeScanStatusWithTimeout(String qrcodeKey) {
        QrcodeStatusResponse.QrcodeStatus qrcodeStatus;
        CompletableFuture<QrcodeStatusResponse.QrcodeStatus> future = CompletableFuture.supplyAsync(() -> getQrcodeScanStatusWithLoop(qrcodeKey));
        try {
            // 二维码有效期2分钟，排除掉5秒的RPC时间
            qrcodeStatus = future.get(115, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            if (!future.isCancelled()) {
                // 强制线程退出，避免无效执行
                future.cancel(true);
            }
            throw new QrcodeScanTimeoutException(e);
        } catch (Exception e) {
            throw new BusinessException("获取企微二维码扫描状态异常,qrcodeKey=" + qrcodeKey, e);
        }
        return qrcodeStatus;
    }

    private QrcodeStatusResponse.QrcodeStatus getQrcodeScanStatusWithLoop(String qrcodeKey) {
        QrcodeStatusResponse.QrcodeStatus qrcodeStatus = null;
        // 总时长:120000(120秒)，总体下来执行时间超过二维码的有效期，通过调用方future强制中断线程。
        int sleepInterval = 3000, executeDuration = 300, allDuration = 120000;
        int loop = allDuration / executeDuration;
        for (int i = 0; i < loop; i++) {
            try {
                qrcodeStatus = loginRpc.getQrcodeScanStatus(qrcodeKey);
            } catch (Exception e) {
                // 忽略
            }
            boolean success = loginRpc.isQrcodeScanSuccess(qrcodeStatus);
            if (success) {
                break;
            }
            Uninterruptibles.sleepUninterruptibly(sleepInterval, TimeUnit.MILLISECONDS);
        }
        return qrcodeStatus;
    }
}
