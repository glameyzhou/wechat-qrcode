package com.jd.o2o.wx.group.admin.wechatqrcode.exception;

/**
 * 二维码状态扫描超时异常
 *
 * @author zhouyang281
 * @date 2023-09-27
 */
public class QrcodeScanTimeoutException extends RuntimeException {

    public QrcodeScanTimeoutException() {
        super();
    }

    public QrcodeScanTimeoutException(String message) {
        super(message);
    }

    public QrcodeScanTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }


    public QrcodeScanTimeoutException(Throwable cause) {
        super(cause);
    }

    protected QrcodeScanTimeoutException(String message, Throwable cause,
                                         boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
