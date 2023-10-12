package com.jd.o2o.wx.group.admin.wechatqrcode.exception;

/**
 * 业务异常
 *
 * @author zhouyang281
 * @date 2023-09-27
 */
public class AccessIllegalException extends RuntimeException {

    public AccessIllegalException() {
        super();
    }

    public AccessIllegalException(String message) {
        super(message);
    }

    public AccessIllegalException(String message, Throwable cause) {
        super(message, cause);
    }


    public AccessIllegalException(Throwable cause) {
        super(cause);
    }

    protected AccessIllegalException(String message, Throwable cause,
                                     boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
