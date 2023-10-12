package com.jd.o2o.wx.group.admin.wechatqrcode.service;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;
import com.jd.o2o.wx.group.admin.wechatqrcode.domain.GroupChatListResponse;
import com.jd.o2o.wx.group.admin.wechatqrcode.service.rpc.GroupChatRpc;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouyang281
 * @date 2023-09-27
 */
@Component
public class GroupChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupChatService.class);

    @Resource
    private GroupChatRpc groupChatRpc;

    public String getQrcodeUrl(String roomId) {
        String qrcodeUrl = groupChatRpc.getGroupChatQrcodeUrl(roomId);
        LOGGER.info("查询社群二维码, roomId={},qrcodeUrl={}", roomId, qrcodeUrl);
        return qrcodeUrl;
    }

    public List<GroupChatListResponse.GroupChat> getGroupChatList(@Nonnull String vid, @Nullable String groupChatName) {
        List<GroupChatListResponse.GroupChat> retList = Lists.newArrayList();
        int offset = 0, limit = 10;
        List<String> vids = Lists.newArrayList(vid);
        List<String> accurateKeywords = StringUtils.isBlank(groupChatName) ? null : Lists.newArrayList(groupChatName);

        // 内部使用(vid+社群名称)精确查询，返回的结果是当前员工创建的同名字的社群集合。
        // 目前的数据查看，不超过20条。不排除群名称为空的情况。考虑下群名称如果是空的话，是否通过群创建时间精确查询
        GroupChatListResponse.GroupChatListData listData;
        while (true) {
            listData = groupChatRpc.getGroupChatList(offset, limit, vids, accurateKeywords, null);
            if (listData == null) {
                break;
            }

            List<GroupChatListResponse.GroupChat> subList = ListUtils.emptyIfNull(listData.getDatalist());
            if (CollectionUtils.isNotEmpty(subList)) {
                retList.addAll(subList);
            }
            if (listData.getNextIndex() < 0) {
                break;
            }
            offset = listData.getNextIndex();
            Uninterruptibles.sleepUninterruptibly(50, TimeUnit.MILLISECONDS);
        }
        return retList;
    }

}
