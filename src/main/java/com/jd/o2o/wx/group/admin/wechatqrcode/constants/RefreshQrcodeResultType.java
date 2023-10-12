package com.jd.o2o.wx.group.admin.wechatqrcode.constants;

import lombok.Getter;

/**
 * @author zhouyang281
 * @date 2023-09-28
 */
public enum RefreshQrcodeResultType {
    NO_OWNER_TO_VID("未查询到userId-vid映射信息"),
    NO_GROUP_CHAT("未查询到社群信息"),
    NO_QRCODE_URL("未查询到社群二维码(可能开启进群验证)"),
    BUSINESS_EXCEPTION("系统异常"),
    SUCCESS("成功");

    @Getter
    private final String desc;

    RefreshQrcodeResultType(String desc) {
        this.desc = desc;
    }
}
