package com.jd.o2o.wx.group.admin.wechatqrcode.interceptor;

import com.jd.o2o.wx.group.admin.wechatqrcode.domain.APIResponse;
import com.jd.o2o.wx.group.admin.wechatqrcode.exception.AccessIllegalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Created by likaihao on 2021/12/07
 */

@Slf4j
@RestControllerAdvice
public class SystemExceptionHandler {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({NoHandlerFoundException.class})
    public APIResponse<Boolean> handleNoFoundException(Exception exception) {
        return APIResponse.fail(404, "NOT FOUND");
    }


    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({AccessIllegalException.class})
    public APIResponse<Boolean> handleAccessIllegal(Exception exception) {
        return APIResponse.fail(401, "未授权，禁止访问");
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({Exception.class})
    public APIResponse<Boolean> handleGlobalException(Exception exception) {
        log.error("异常", exception);
        return APIResponse.fail(500, StringUtils.defaultIfBlank(exception.getMessage(), "服务开小车了，稍后重试..."));
    }
}
