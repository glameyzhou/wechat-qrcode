package com.jd.o2o.wx.group.admin.wechatqrcode.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author zhouyang281
 * @date 2023-09-28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpRecord {

    private String url;

    private Map<String, List<String>> requestHeader;

    private String requestBody;

    private int responseCode;

    private Map<String, List<String>> responseHeader;

    private String responseBody;
}
