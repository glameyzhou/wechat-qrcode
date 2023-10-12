package com.jd.o2o.wx.group.admin.wechatqrcode.service;

import org.springframework.stereotype.Component;

/**
 * @author zhouyang281
 * @date 2023-10-07
 */
@Component
public class ConfigService {
/*
    @Resource
    private ConfiguratorManager configuratorManager;

    public <T> T getProperty(String key, Class<T> clazz, T defaultValue) {
        Property property = configuratorManager.getProperty(key);
        if (property == null) {
            return defaultValue;
        }

        String origin = property.getString();
        if (origin == null) {
            return defaultValue;
        }
        T t = JSON.parseObject(origin, clazz);
        return t == null ? defaultValue : t;
    }

    public <T> List<T> getProperty(String key, Class<T> clazz, List<T> defaultValue) {
        Property property = configuratorManager.getProperty(key);
        if (property == null) {
            return defaultValue;
        }

        String origin = property.getString();
        if (origin == null) {
            return defaultValue;
        }
        List<T> list = JSON.parseArray(origin, clazz);
        return list == null ? defaultValue : list;
    }


    public <T> List<T> getList(String key, Class<T> clazz, List<T> defaultValue) {
        return getProperty(key, clazz, defaultValue);
    }*/
}
