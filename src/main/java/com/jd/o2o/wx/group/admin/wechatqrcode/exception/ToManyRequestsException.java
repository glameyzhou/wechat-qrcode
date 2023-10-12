package com.jd.o2o.wx.group.admin.wechatqrcode.exception;

/**
 * 请求太多异常 http status code = 429
 *
 * @author zhouyang281
 * @date 2023-09-27
 */
public class ToManyRequestsException extends RuntimeException {

    public ToManyRequestsException() {
        super();
    }

    public ToManyRequestsException(String message) {
        super(message);
    }

    public ToManyRequestsException(String message, Throwable cause) {
        super(message, cause);
    }


    public ToManyRequestsException(Throwable cause) {
        super(cause);
    }

    protected ToManyRequestsException(String message, Throwable cause,
                                      boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
