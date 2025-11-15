package com.macro.mall.portal.service.impl;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.UmsIntegrationChangeHistoryMapper;
import com.macro.mall.mapper.UmsMemberCheckinMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.service.UmsMemberCheckinService;
import com.macro.mall.portal.service.UmsMemberService;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 会员签到服务实现
 */
@Service
public class UmsMemberCheckinServiceImpl implements UmsMemberCheckinService {

    @Autowired
    private UmsMemberCheckinMapper checkinMapper;

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsIntegrationChangeHistoryServiceImpl integrationChangeHistoryService;

    @Autowired
    private UmsMemberService memberService;

    @Override
    @Transactional
    public boolean checkin(Long memberId) {
        Date date = new Date();
        // 检查是否已签到
        if (isChecked(memberId, date)) {
            return false;
        }

        // 创建签到记录
        UmsMemberCheckin checkin = new UmsMemberCheckin();
        checkin.setMemberId(memberId);
        checkin.setCheckinDate(date);
        if(getContinuousDays(memberId)+1<7) {
            checkin.setIntegration(10); // 签到获得10积分
        }else{
            checkin.setIntegration(20);
        }
        checkin.setCreateTime(new Date()); // 设置创建时间
        checkin.setIsRecheckin(0); // 非补签
        checkinMapper.insert(checkin);
        // 更新会员积分
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        if (member != null) {
            UmsMember updateMember = new UmsMember();
            updateMember.setId(memberId);
            // 处理积分字段可能为null的情况
            Integer currentIntegration = member.getIntegration();
            if (currentIntegration == null) {
                currentIntegration = 0;
            }
            memberService.updateIntegration(memberId, currentIntegration + checkin.getIntegration());

            // 记录积分变化历史
            integrationChangeHistoryService.addIntegrationChangeHistory(memberId, 0, checkin.getIntegration(), 2, "签到获得积分");
        }
        return true;
    }

    @Override
    @Transactional
    public boolean retroactiveCheckin(Long memberId, Date date) {
        // 检查是否已签到
        if (isChecked(memberId, date)) {
            return false;
        }

        // 创建签到记录
        UmsMemberCheckin checkin = new UmsMemberCheckin();
        checkin.setMemberId(memberId);
        checkin.setCheckinDate(date);
        checkin.setIntegration(-100); // 补签扣除100积分
        checkin.setCreateTime(new Date()); // 设置创建时间
        checkin.setIsRecheckin(1); // 补签
        checkinMapper.insert(checkin);

        // 更新会员积分
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        if (member != null) {
            UmsMember updateMember = new UmsMember();
            updateMember.setId(memberId);
            // 处理积分字段可能为null的情况
            Integer currentIntegration = member.getIntegration();
            if (currentIntegration == null) {
                currentIntegration = 0;
            }
            memberService.updateIntegration(memberId, currentIntegration - 100);

            // 记录积分变化历史
            integrationChangeHistoryService.addIntegrationChangeHistory(memberId, 1, -100, 3, "补签扣除积分");
        }

        return true;
    }


    @Override
    public List<UmsMemberCheckin> getCheckinHistory(Long memberId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        UmsMemberCheckinExample example = new UmsMemberCheckinExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        example.setOrderByClause("checkin_date DESC");
        return checkinMapper.selectByExample(example);
    }

    @Override
    public List<UmsMemberCheckin> getCheckinHistoryByMonth(Long memberId, Date month) {
        UmsMemberCheckinExample example = new UmsMemberCheckinExample();
        example.createCriteria()
                .andMemberIdEqualTo(memberId)
                .andCheckinDateGreaterThanOrEqualTo(DateUtil.beginOfMonth(month))
                .andCheckinDateLessThanOrEqualTo(DateUtil.endOfMonth(month));
        example.setOrderByClause("checkin_date DESC");
        return checkinMapper.selectByExample(example);
    }

    @Override
   public Integer getContinuousDays(Long memberId) {
    int continuousDays = 0;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());

    // 检查今天是否签到
    UmsMemberCheckinExample todayExample = new UmsMemberCheckinExample();
    todayExample.createCriteria()
            .andMemberIdEqualTo(memberId)
            .andCheckinDateEqualTo(DateUtil.beginOfDay(calendar.getTime()));
    List<UmsMemberCheckin> todayCheckins = checkinMapper.selectByExample(todayExample);

    if (todayCheckins.isEmpty()) {
        // 如果今天未签到，则调整日期到昨天
        calendar.add(Calendar.DAY_OF_MONTH, -1);
    }

    // 检查连续签到天数
    while (true) {
        UmsMemberCheckinExample example = new UmsMemberCheckinExample();
        example.createCriteria()
                .andMemberIdEqualTo(memberId)
                .andCheckinDateEqualTo(DateUtil.beginOfDay(calendar.getTime()));
        List<UmsMemberCheckin> checkins = checkinMapper.selectByExample(example);

        if (checkins.isEmpty()) {
            break;
        }

        continuousDays++;
        calendar.add(Calendar.DAY_OF_MONTH, -1); // 前一天
    }

    return continuousDays;
}


    @Override
    public boolean isChecked(Long memberId, Date checkinDate) {
        // 标准化日期（去除时分秒）
        Date normalizedDate = normalizeDate(checkinDate);
        UmsMemberCheckinExample example = new UmsMemberCheckinExample();
        example.createCriteria()
                .andMemberIdEqualTo(memberId)
                .andCheckinDateEqualTo(normalizedDate);
        List<UmsMemberCheckin> checkins = checkinMapper.selectByExample(example);
        System.out.println(checkins);
        return !checkins.isEmpty();
    }

    // 日期标准化工具方法
    private Date normalizeDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}

