package com.macro.mall.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.UmsMemberLevelMapper;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.model.UmsMemberLevelExample;
import com.macro.mall.service.UmsMemberLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 会员等级管理Service实现类
 */
@Service
public class UmsMemberLevelServiceImpl implements UmsMemberLevelService {

    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;

    @Override
    public Page<UmsMemberLevel> list(Integer defaultStatus, String name, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        UmsMemberLevelExample example = new UmsMemberLevelExample();

        // 创建条件对象
        UmsMemberLevelExample.Criteria criteria = example.createCriteria();

        // 1. 添加defaultStatus条件（非空时生效）
        if (defaultStatus != null) {
            criteria.andDefaultStatusEqualTo(defaultStatus);
        }

        // 2. 新增name模糊查询条件（非空时生效）
        if (!StringUtils.isEmpty(name) && !name.isEmpty()) {
            criteria.andNameLike("%" + name + "%");  // 模糊匹配
            // 或精确查询：criteria.andNameEqualTo(name);
        }

        // 3. 按id排序
        example.setOrderByClause("growth_point desc");  // 建议明确排序方向

        return (Page<UmsMemberLevel>) memberLevelMapper.selectByExample(example);
    }


    @Override
    public int create(UmsMemberLevel memberLevel) {
        return memberLevelMapper.insertSelective(memberLevel);
    }

    @Override
    public UmsMemberLevel getItem(Long id) {
        return memberLevelMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(Long id, UmsMemberLevel memberLevel) {
        memberLevel.setId(id);
        return memberLevelMapper.updateByPrimaryKeySelective(memberLevel);
    }

    @Override
    public int delete(Long id) {
        return memberLevelMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int delete(List<Long> ids) {
        UmsMemberLevelExample example = new UmsMemberLevelExample();
        example.createCriteria().andIdIn(ids);
        return memberLevelMapper.deleteByExample(example);
    }
}
