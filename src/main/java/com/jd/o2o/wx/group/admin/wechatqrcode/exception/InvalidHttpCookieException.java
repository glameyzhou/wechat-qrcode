package com.jd.o2o.wx.group.admin.wechatqrcode.exception;

/**
 * cookie失效异常
 *
 * @author zhouyang281
 * @date 2023-09-27
 */
public class InvalidHttpCookieException extends RuntimeException {

    public InvalidHttpCookieException() {
        super();
    }

    public InvalidHttpCookieException(String message) {
        super(message);
    }

    public InvalidHttpCookieException(String message, Throwable cause) {
        super(message, cause);
    }


    public InvalidHttpCookieException(Throwable cause) {
        super(cause);
    }

    protected InvalidHttpCookieException(String message, Throwable cause,
                                         boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
