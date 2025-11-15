package com.macro.mall.service;

import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.model.UmsRole;

import java.util.List;

public interface UmsMemberService {
    /**
     * 查询所有会员
     */
    List<UmsMember> list(UmsMemberExample member);

    /**
     * 分页查询会员
     */
    List<UmsMember> list(UmsMember member, Integer pageSize, Integer pageNum);

    /**
     * 获取会员积分详情
     */
    List<UmsIntegrationChangeHistory> integrationChangeHistoryList(Long memberId, Integer pageSize, Integer pageNum);

    /**
     * 添加会员
     */
    int create(UmsMember role);

    /**
     * 修改会员信息
     */
    int update(Long id, UmsMember role);

    /**
     * 批量删除会员
     */
    int delete(List<Long> ids);
}
