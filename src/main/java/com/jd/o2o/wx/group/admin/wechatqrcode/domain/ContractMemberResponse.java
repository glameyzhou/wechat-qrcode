package com.jd.o2o.wx.group.admin.wechatqrcode.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 通讯录员工信息对象
 *
 * @author zhouyang281
 * @date 2023-09-27
 */
@Data
public class ContractMemberResponse {

    private ContractMemberData data;

    @Data
    public static class ContractMemberData {

        /**
         * 当前成员列表
         */
        @JSONField(name = "mems")
        private List<ContractMember> memberList;

        /**
         * 已离开成员列表
         */
        @JSONField(name = "leavingMems")
        private List<ContractMember> leavingMemberList;
    }

    @Data
    public static class ContractMember {

        /**
         * 用户ID(数字类型)
         */
        private String vid;

        /**
         * !!!!IMPORT!!!!
         * 账号userId(离职对象集合中没有此字段)
         */
        @JSONField(name = "acctid")
        private String userId;

        /**
         * 姓名
         */
        private String name;

        /**
         * 别名
         */
        private String uid;

        /**
         * 手机号
         */
        private String mobile;
    }
}
