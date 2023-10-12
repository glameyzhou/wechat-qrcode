package com.jd.o2o.wx.group.admin.wechatqrcode.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 社群列表对象，包含翻页
 *
 * @author zhouyang281
 * @date 2023-09-27
 */
@Data
public class GroupChatListResponse {

    private GroupChatListData data;

    @Data
    public static class GroupChatListData {

        private List<GroupChat> datalist;

        private int total;

        /**
         * -1表示最后一页
         */
        @JSONField(name = "next_index")
        private int nextIndex;

        @JSONField(name = "next_page_buff")
        private String nextPageBuff;
    }

    @Data
    public static class GroupChat {

        private String id;

        @JSONField(name = "roomid")
        private String roomId;

        @JSONField(name = "roomname")
        private String roomName;

        @JSONField(name = "adminName")
        private String adminName;

        private String vid;

        /**
         * 群创建时间，单位绝对秒。
         */
        @JSONField(name = "createtime")
        private long createTime;

        /**
         * 群更新时间，单位绝对秒
         */
        @JSONField(name = "updatetime")
        private long updateTime;
    }
}
