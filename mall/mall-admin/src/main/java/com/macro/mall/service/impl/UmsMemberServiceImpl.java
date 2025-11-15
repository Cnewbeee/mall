package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.UmsIntegrationChangeHistoryMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.model.UmsIntegrationChangeHistoryExample;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsIntegrationChangeHistoryMapper integrationChangeHistoryMapper;

    @Override
    public List<UmsMember> list(UmsMemberExample member) {
        List<UmsMember> memberList = memberMapper.selectByExample(member);
        return memberList;
    }

    @Override
    public List<UmsMember> list(UmsMember member, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        // 对需要模糊查询的字段进行处理
        if (member != null) {
            if (member.getUsername() != null) {
                member.setUsername("%" + member.getUsername() + "%");
            }
            if (member.getPhone() != null) {
                member.setPhone("%" + member.getPhone() + "%");
            }
            if (member.getNickname() != null) {
                member.setNickname("%" + member.getNickname() + "%");
            }
        }
        List<UmsMember> memberList = memberMapper.selectList(member);
        return memberList;
    }
    @Override
    public List<UmsIntegrationChangeHistory> integrationChangeHistoryList(Long memberId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsIntegrationChangeHistoryExample example = new UmsIntegrationChangeHistoryExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        List<UmsIntegrationChangeHistory> integrationChangeHistoryList = integrationChangeHistoryMapper.selectByExample(example);
        return integrationChangeHistoryList;
    }
    @Override
    public int create(UmsMember role) {
        role.setPassword(passwordEncoder.encode(role.getPassword()));
        return memberMapper.insert(role);
    }

    @Override
    public int update(Long id, UmsMember role) {
        UmsMember oldMember = memberMapper.selectByPrimaryKey(id);
        role.setId(id);
        role.setPassword(passwordEncoder.encode(role.getPassword()));
        if(oldMember.getIntegration() != role.getIntegration()){
            UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
            history.setMemberId(id);
            history.setCreateTime(new Date());
            if(oldMember.getIntegration() > role.getIntegration()){
                history.setChangeType(1);
            }else{
                history.setChangeType(0);
            }
            history.setChangeCount(role.getIntegration() - oldMember.getIntegration());
            history.setOperateMan("后台管理员");
            history.setOperateNote("修改积分");
            history.setSourceType(1);
            integrationChangeHistoryMapper.insert(history);
        }
        return memberMapper.updateByPrimaryKey(role);
    }

    @Override
    public int delete(List<Long> ids) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andIdIn(ids);
        return memberMapper.deleteByExample(example);
    }
}
