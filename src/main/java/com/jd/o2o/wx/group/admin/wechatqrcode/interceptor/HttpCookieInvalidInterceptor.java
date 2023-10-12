package com.jd.o2o.wx.group.admin.wechatqrcode.interceptor;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.jd.o2o.wx.group.admin.wechatqrcode.domain.HttpRecord;
import com.jd.o2o.wx.group.admin.wechatqrcode.exception.InvalidHttpCookieException;
import com.jd.o2o.wx.group.admin.wechatqrcode.utils.RestUtils;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Optional;

/**
 * http cookie失效拦截
 *
 * @author zhouyang281
 * @date 2023-09-27
 */
public class HttpCookieInvalidInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpCookieInvalidInterceptor.class);

    @Override
    @Nonnull
    public Response intercept(@Nonnull Chain chain) {
        Request request = chain.request();
        Response response;
        HttpRecord.HttpRecordBuilder recordBuilder = HttpRecord.builder();
        try {

            response = chain.proceed(request);

            // 收集HTTP请求与响应
            recordBuilder.url(request.url().toString());
            recordBuilder.requestHeader(request.headers().toMultimap());
            recordBuilder.requestBody(extractParams(request.body()));
            recordBuilder.responseCode(response.code());
            recordBuilder.responseHeader(response.headers().toMultimap());
            String responseBody = extractResponseBody(response.body());
            recordBuilder.responseBody(responseBody);
            if (isInvalidCookie(responseBody)) {
                throw new InvalidHttpCookieException();
            }
        /*} catch (InvalidHttpCookieException e) {
            LOGGER.error("InvalidHttpCookieException, url={}", request.url(), e);*/
        } catch (Exception e) {
            throw new RuntimeException("execute http error, url=" + request.url(), e);
        } finally {
            LOGGER.info("HTTP Interceptor -> {}", recordBuilder.toString());
        }
        return response;
    }

    private boolean isInvalidCookie(String response) {
        // 非JSON
        response = StringUtils.trim(response);
        if (!(StringUtils.startsWith(response, "[") || StringUtils.startsWith(response, "{"))) {
            return false;
        }
        // cookie错误  {"result":{"errCode":-3,"message":"outsession"}}
        // 他处登陆    {"result":{"errCode":-3,"message":"outsession","etype":"otherLogin"}}
        String code = Optional.ofNullable(JSON.parseObject(response))
                              .map(x -> x.getJSONObject("result"))
                              .map(x -> x.getString("errCode"))
                              .orElse("");
        return StringUtils.equals(code, "-3");
    }


    private String extractResponseBody(ResponseBody responseBody) throws IOException {
        if (responseBody == null) {
            return null;
        }
        BufferedSource source = responseBody.source();
        source.request(responseBody.contentLength() > 0 ? responseBody.contentLength() : Integer.MAX_VALUE);
        Buffer buffer = source.getBuffer();

        Charset charset = Charsets.UTF_8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(Charsets.UTF_8);
            } catch (UnsupportedCharsetException e) {
                LOGGER.error("解析请求体异常", e);
            }
        }
        if (!isPlaintext(buffer)) {
            return null;
        }
        if (responseBody.contentLength() != 0) {
            charset = charset == null ? Charsets.UTF_8 : charset;
            return buffer.clone().readString(charset);
        }
        return null;
    }

    private boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false;
        }
    }

    private String extractParams(RequestBody requestBody) {
        if (requestBody == null) {
            return null;
        }
        Buffer buffer = new Buffer();
        String params;
        try {
            requestBody.writeTo(buffer);
            params = buffer.readUtf8();
            params = RestUtils.decode(params);
        } catch (IOException e) {
            LOGGER.error("解析请求参数异常", e);
            return "";
        }
        return params;
    }
}
