package com.jd.o2o.wx.group.admin.wechatqrcode.domain;

import com.google.common.collect.ArrayListMultimap;
import com.jd.o2o.wx.group.admin.wechatqrcode.constants.RefreshQrcodeResultType;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhouyang281
 * @date 2023-09-28
 */
@Data
public class RefreshQrcodeResult {

    /**
     * 总行数
     */
    private AtomicInteger total = new AtomicInteger();

    /**
     * 成功的行数
     */
    private AtomicInteger successTotal = new AtomicInteger();

    /**
     * 失败数据
     * key=刷新结果状态
     * value=dbId;dbChatId;dbChatName {@link com.jd.o2o.wx.group.domain.entity.WxGroupChat}
     */
    private ArrayListMultimap<RefreshQrcodeResultType, String> failTypeList = ArrayListMultimap.create();


    public void addTotal() {
        total.incrementAndGet();
    }

    public void addSuccessTotal() {
        successTotal.incrementAndGet();
    }

    public synchronized void addFail(RefreshQrcodeResultType resultType, String source) {
        failTypeList.put(resultType, source);
    }
}
