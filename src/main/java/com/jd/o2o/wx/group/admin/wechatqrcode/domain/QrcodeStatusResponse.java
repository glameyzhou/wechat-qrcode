package com.jd.o2o.wx.group.admin.wechatqrcode.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 登陆二维码扫描状态
 *
 * @author zhouyang281
 * @date 2023-09-2023/9/27
 */
@Data
public class QrcodeStatusResponse {

    private QrcodeStatus data;

    @Data
    public static class QrcodeStatus {

        private String status;

        @JSONField(name = "auth_source")
        private String authSource;

        @JSONField(name = "auth_code")
        private String authCode;
    }
}
