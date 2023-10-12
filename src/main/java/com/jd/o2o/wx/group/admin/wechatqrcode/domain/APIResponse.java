package com.jd.o2o.wx.group.admin.wechatqrcode.domain;

import lombok.Data;

/**
 * @author zhouyang281
 * @date 2023-10-12
 */
@Data
public class APIResponse<T> {
    private int code;
    private String message;
    private long timestamp;
    private T data;

    public static <T> APIResponse success(T data) {
        APIResponse<T> response = new APIResponse<>();
        response.setCode(0);
        response.setMessage("success");
        response.setTimestamp(System.currentTimeMillis());
        response.setData(data);
        return response;
    }

    public static <T> APIResponse fail(int code, String message) {
        APIResponse<T> response = new APIResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}
