package com.jd.o2o.wx.group.admin.wechatqrcode.service;

import com.google.common.collect.Maps;
import com.jd.o2o.wx.group.admin.wechatqrcode.constants.WecomAdminConstants;
import com.jd.o2o.wx.group.admin.wechatqrcode.domain.ContractMemberResponse;
import com.jd.o2o.wx.group.admin.wechatqrcode.service.rpc.ContactRpc;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 联系人服务
 *
 * @author zhouyang281
 * @date 2023-09-27
 */
@Component
public class ContactService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactService.class);
    @Resource
    private ContactRpc contactRpc;
    private static final Map<String, String> USER_ID_VID_MAPPING = Maps.newConcurrentMap();
    private final Object lock = new Object();

    /**
     * 通过员工userId换vid，本地缓存。缓存可以通过定时任务刷新(广播形式)，也可以本地schedule处理
     *
     * @param userId 企微员工唯一标识userId
     * @return vid   企微员工vid,vid-userId一一对应。
     */
    public String getMemberVidByUserId(String userId) {
        userId = WecomAdminConstants.CacheKeyPrefix.WECOM_CONTRACT_USERID_VID_ + userId;
        if (USER_ID_VID_MAPPING.size() == 0) {
            synchronized (lock) {
                if (USER_ID_VID_MAPPING.size() == 0) {
                    storeContactMemberToLocalCache();
                }
            }
        }
        return USER_ID_VID_MAPPING.get(userId);
    }

    public void storeContactMemberToLocalCache() {
        ContractMemberResponse.ContractMemberData contractMemberData = contactRpc.getContractMemberList();
        if (contractMemberData == null) {
            LOGGER.error("获取企微员工列表-API返回NULL");
            return;
        }
        if (CollectionUtils.isEmpty(contractMemberData.getMemberList())) {
            LOGGER.error("获取企微员工列表-返回NULL");
            return;
        }
        LOGGER.info("企微员工userId-vid映射关系放入缓存，size={}", CollectionUtils.size(contractMemberData.getMemberList()));
//        contractMemberData.getMemberList().forEach(member -> USER_ID_VID_MAPPING.put(member.getUserId(), member.getVid()));
        contractMemberData.getMemberList().forEach(member -> {
            // 暂定缓存保留二天，数据通过心跳会实时更新覆盖
            String key = WecomAdminConstants.CacheKeyPrefix.WECOM_CONTRACT_USERID_VID_ + member.getUserId();
            USER_ID_VID_MAPPING.put(key, member.getVid());
        });
    }
}
