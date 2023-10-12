package com.jd.o2o.wx.group.admin.wechatqrcode.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.o2o.wx.group.admin.wechatqrcode.constants.WecomAdminConstants;
import com.jd.o2o.wx.group.admin.wechatqrcode.exception.ToManyRequestsException;
import com.jd.o2o.wx.group.admin.wechatqrcode.interceptor.HttpCookieInvalidInterceptor;
import okhttp3.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

/**
 * 网络请求工具
 *
 * @author zhouyang281
 * @date 2023-09-2023/9/27
 */
public class RestUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestUtils.class);

    private final static OkHttpClient COOKIE_HTTP_CLIENT = new OkHttpClient.Builder()
            .followRedirects(false)
            .followSslRedirects(false)
            .cookieJar(customCookieJar())
            .connectTimeout(Duration.ofSeconds(6))
            .readTimeout(Duration.ofSeconds(20))
            .connectionPool(new ConnectionPool(100, 30L, TimeUnit.MINUTES))
            .sslSocketFactory(CustomSSLSocket.getSSLSocketFactory(), CustomSSLSocket.getX509TrustManager())
            .hostnameVerifier(CustomSSLSocket.getHostnameVerifier())
            .addInterceptor(new HttpCookieInvalidInterceptor())
            .build();

    private final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(6))
            .readTimeout(Duration.ofSeconds(20))
            .connectionPool(new ConnectionPool(100, 30L, TimeUnit.MINUTES))
            .sslSocketFactory(CustomSSLSocket.getSSLSocketFactory(), CustomSSLSocket.getX509TrustManager())
            .hostnameVerifier(CustomSSLSocket.getHostnameVerifier())
            .addInterceptor(new HttpCookieInvalidInterceptor())
            .build();

    private static CookieJar customCookieJar() {
        return new CookieJar() {
            final LinkedHashMap<String, List<Cookie>> cookieStore = new LinkedHashMap<>();

            @Override
            public void saveFromResponse(@Nonnull HttpUrl httpUrl, @Nonnull List<Cookie> list) {
                cookieStore.put(httpUrl.host(), list);
            }

            @Nonnull
            @Override
            public List<Cookie> loadForRequest(@Nonnull HttpUrl httpUrl) {
                List<Cookie> cookies = cookieStore.get(httpUrl.host());
                return cookies != null ? cookies : Lists.newArrayList();
            }
        };
    }

    private static void buildHeader(Request.Builder builder, Map<String, String> headers) {
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach((k, v) -> {
                if (k != null && v != null) {
                    builder.addHeader(k, v);
                }
            });
        }
    }


    public static String get(String url, Map<String, String> headers) {
        try {
            Request.Builder builder = new Request.Builder();
            buildHeader(builder, headers);
            Request request = builder.url(url).build();
            try (Response response = HTTP_CLIENT.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                }
                if (response.code() == WecomAdminConstants.Network.HTTP_CODE_TO_MANY_REQUESTS) {
                    throw new ToManyRequestsException("请求频繁" + url);
                }
            }
        } catch (Exception e) {
            LOGGER.error("execute get error，url={}", url, e);
        }
        return null;
    }

    public static <T> T get(String url, Map<String, String> headers, TypeReference<T> typeReference) {
        try {
            String result;
            Request.Builder builder = new Request.Builder();
            buildHeader(builder, headers);
            Request request = builder.url(url).build();
            try (Response response = HTTP_CLIENT.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    result = response.body().string();
                    return JSON.parseObject(result, typeReference);
                }
                if (response.code() == WecomAdminConstants.Network.HTTP_CODE_TO_MANY_REQUESTS) {
                    throw new ToManyRequestsException("请求频繁" + url);
                }
            }
        } catch (Exception e) {
            LOGGER.error("execute get error，url={}", url, e);
        }
        return null;
    }

    public static String postForm(String url, Map<String, String> headers, Map<String, String> params) {
        try {
            Request.Builder builder = new Request.Builder();
            buildHeader(builder, headers);
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            if (MapUtils.isNotEmpty(params)) {
                params.forEach(bodyBuilder::addEncoded);
            }
            Request request = builder.url(url).post(bodyBuilder.build()).build();
            try (Response response = HTTP_CLIENT.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                }
                if (response.code() == WecomAdminConstants.Network.HTTP_CODE_TO_MANY_REQUESTS) {
                    throw new ToManyRequestsException("请求频繁" + url);
                }
            }
        } catch (Exception e) {
            LOGGER.error("execute get error，url={}", url, e);
        }
        return null;
    }

    public static String postJson(String url, Map<String, String> headers, Object body) {
        return postJson(url, headers, body, false);
    }

    public static String postJson(String url, Map<String, String> headers, Object body, boolean json) {
        try {
            Request.Builder builder = new Request.Builder();
            buildHeader(builder, headers);
            String bodyStr = json ? (String) body : JSON.toJSONString(body);
            RequestBody requestBody = RequestBody.create(MediaType.parse(WecomAdminConstants.Header.CONTENT_TYPE_JSON_VALUE), bodyStr);
            Request request = builder.url(url).post(requestBody).build();
            try (Response response = HTTP_CLIENT.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                }
                if (response.code() == WecomAdminConstants.Network.HTTP_CODE_TO_MANY_REQUESTS) {
                    throw new ToManyRequestsException("请求频繁" + url);
                }
            }
        } catch (Exception e) {
            LOGGER.error("execute get error，url={}", url, e);
        }
        return null;
    }

    public static String getCookieByRedirect(String url, Map<String, String> headers) {
        try {
            Request.Builder builder = new Request.Builder();
            buildHeader(builder, headers);
            Request request = builder.url(url).build();
            try (Response response = COOKIE_HTTP_CLIENT.newCall(request).execute()) {
                if (response.isRedirect()) {
                    String location = response.header(WecomAdminConstants.Header.Location);
                    if (StringUtils.isBlank(location)) {
                        throw new RuntimeException("response is redirect, location is null, url=" + url);
                    }
                    String resetCookies = mergeCookieOf302(headers.get(WecomAdminConstants.Header.COOKIE), COOKIE_HTTP_CLIENT.cookieJar().loadForRequest(request.url()));
                    headers = Maps.newHashMap(headers);
                    headers.put(WecomAdminConstants.Header.COOKIE, resetCookies);
                    String locationUrl = WecomAdminConstants.Domain.BASE_DOMAIN + location;
                    return getCookieByRedirect(locationUrl, headers);
                }
                if (response.isSuccessful() && response.body() != null) {
                    return mergeCookieOf200(headers.get(WecomAdminConstants.Header.COOKIE), response.headers(WecomAdminConstants.Header.SET_COOKIE));
                }
            }
        } catch (Exception e) {
            LOGGER.error("execute get error，url={}", url, e);
        }
        return null;
    }

    private static String mergeCookieOf302(String originCookie, List<Cookie> cookies) {
        Map<String, String> finalCookies = Maps.newHashMap();

        extractCookieToMap(originCookie, finalCookies);

        for (Cookie cookie : cookies) {
            finalCookies.put(cookie.name(), StringUtils.defaultIfBlank(cookie.value(), ""));
        }
        return finalCookies.entrySet().stream()
                           .map(entry -> entry.getKey() + "=" + entry.getValue())
                           .collect(Collectors.joining("; "));
    }

    private static String mergeCookieOf200(String originCookie, List<String> setCookies) {
        Map<String, String> finalCookies = Maps.newHashMap();

        extractCookieToMap(originCookie, finalCookies);

        for (String cookie : setCookies) {
            List<String> strings = emptyIfNull(Splitter.on(";").trimResults().splitToList(cookie));
            if (CollectionUtils.size(strings) > 0) {
                List<String> kv = Splitter.on("=").trimResults().splitToList(strings.get(0));
                if (CollectionUtils.size(kv) == 2) {
                    finalCookies.put(kv.get(0), StringUtils.defaultIfBlank(kv.get(1), ""));
                }
            }
        }
        return finalCookies.entrySet().stream()
                           .map(entry -> entry.getKey() + "=" + entry.getValue())
                           .collect(Collectors.joining("; "));
    }

    private static void extractCookieToMap(String cookieString, Map<String, String> cookieMap) {
        if (StringUtils.isBlank(cookieString)) {
            return;
        }
        List<String> originCookieList = emptyIfNull(WecomAdminConstants.Tool.SPLITTER_SEMICOLON.splitToList(cookieString));
        for (String c : originCookieList) {
            List<String> kv = WecomAdminConstants.Tool.SPLITTER_EQUALS.splitToList(c);
            if (CollectionUtils.size(kv) == 2 && StringUtils.isNotBlank(kv.get(1))) {
                cookieMap.put(kv.get(0), kv.get(1));
            }
        }
    }

    @Nonnull
    public static String encode(@Nonnull String source) {
        try {
            return URLEncoder.encode(source, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    public static String decode(@Nonnull String source) {
        try {
            return URLDecoder.decode(source, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
