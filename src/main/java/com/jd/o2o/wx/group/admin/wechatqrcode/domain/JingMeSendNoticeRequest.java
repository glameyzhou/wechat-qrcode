package com.jd.o2o.wx.group.admin.wechatqrcode.domain;

import lombok.Data;

import java.util.List;

/**
 * http://regist-open.timline.jd.com/#/api-list?code=DVTctElN2UX3FsZs45bcGHm2UyUAyF4l-kT5Wj779bk&state=mH6VXnLt62hUwC8Ax_2Fv-XxnpJxJd7kOtegCzjW_uw
 *
 * @author zhouyang281
 * @date 2023-09-27
 */
@Data
public class JingMeSendNoticeRequest {
    /**
     * string 必须	长度不超过100个字符
     */
    private String title;

    private String content;

    private List<String> tos;

    private Extend extend;

    private Infox infox;


    @Data
    public static class Extend {

        private String pic;

        private String url;

        private String btnNameCn;

        private String btnNameEn;
    }

    @Data
    public static class Infox {

        private String deepLink;
    }

}
