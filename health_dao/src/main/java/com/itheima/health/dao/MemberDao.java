package com.itheima.health.dao;

import com.itheima.health.pojo.Member;

public interface MemberDao {
    /**
     * 手机号码查询是否存在
     * @param telephone
     * @return
     */
    Member findByTelephone(String telephone);

    /**
     * 添加会员
     * @param member
     */
    void add(Member member);
}
