package com.macro.mall.service;

import com.macro.mall.model.UmsMemberLevel;
import com.github.pagehelper.Page;
import java.util.List;

/**
 * 会员等级管理Service
 */
public interface UmsMemberLevelService {

    /**
     * 分页查询会员等级
     * @param defaultStatus 是否为默认会员
     * @param pageNum 页码
     * @param pageSize 每页数量
     */
    Page<UmsMemberLevel> list(Integer defaultStatus, String name, Integer pageNum, Integer pageSize);

    /**
     * 创建会员等级
     * @param memberLevel 会员等级信息
     * @return 创建数量
     */
    int create(UmsMemberLevel memberLevel);

    /**
     * 获取会员等级详情
     * @param id 会员等级ID
     * @return 会员等级详情
     */
    UmsMemberLevel getItem(Long id);

    /**
     * 更新会员等级
     * @param id 会员等级ID
     * @param memberLevel 更新后的会员等级信息
     * @return 更新数量
     */
    int update(Long id, UmsMemberLevel memberLevel);

    /**
     * 删除会员等级
     * @param id 会员等级ID
     * @return 删除数量
     */
    int delete(Long id);

    /**
     * 批量删除会员等级
     * @param ids 会员等级ID列表
     * @return 删除数量
     */
    int delete(List<Long> ids);
}
