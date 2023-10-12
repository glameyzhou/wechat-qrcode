package com.jd.o2o.wx.group.admin.wechatqrcode.service;

import com.jd.o2o.wx.group.admin.wechatqrcode.constants.WecomAdminConstants;
import com.jd.o2o.wx.group.admin.wechatqrcode.exception.InvalidHttpCookieException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 后续替换为远端的redis
 *
 * @author zhouyang281
 * @date 2023-09-27
 */
@Component
public class CookieRepository {


    private static final ConcurrentHashMap<String, String> CACHE = new ConcurrentHashMap<>();

    public String load(@Nonnull String key) {
        key = WecomAdminConstants.CacheKeyPrefix.WECOM_COOKIE + key;
        String cookie = CACHE.get(key);
        if (StringUtils.isBlank(cookie)) {
            throw new InvalidHttpCookieException("无Cookie[" + key + "]");
        }
        return cookie;
    }

    public void store(@Nonnull String key, @Nonnull String value) {
        key = WecomAdminConstants.CacheKeyPrefix.WECOM_COOKIE + key;
        CACHE.put(key, value);
    }

    public void remove(@Nonnull String key) {
        key = WecomAdminConstants.CacheKeyPrefix.WECOM_COOKIE + key;
        CACHE.remove(key);
    }
}
