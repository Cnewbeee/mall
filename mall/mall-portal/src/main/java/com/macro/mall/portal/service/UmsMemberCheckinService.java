package com.macro.mall.portal.service;

import com.macro.mall.model.UmsMemberCheckin;

import java.util.Date;
import java.util.List;

/**
 * 会员签到服务
 */
public interface UmsMemberCheckinService {

    /**
     * 会员签到
     * @param memberId 会员ID
     * @return 签到结果
     */
    boolean checkin(Long memberId);

    /**
     * 补签功能
     * @param memberId 用户ID
     * @param targetDate 补签日期（只保留年月日部分）
     * @return 是否补签成功
     */
    boolean retroactiveCheckin(Long memberId, Date targetDate);

    /**
     * 获取会员签到记录
     * @param memberId 会员ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 签到记录列表
     */
    List<UmsMemberCheckin> getCheckinHistory(Long memberId, Integer pageNum, Integer pageSize);

    /**
     * 获取会员月签到记录
     * @param memberId 会员ID
     * @param month 月份
     * @return 月签到记录列表
     */
    List<UmsMemberCheckin> getCheckinHistoryByMonth(Long memberId, Date month);
    /**
     * 获取会员连续签到天数
     * @param memberId 会员ID
     * @return 连续签到天数
     */
    Integer getContinuousDays(Long memberId);

    /**
     * 检查今日是否已签到
     * @param memberId 会员ID
     * @return 是否已签到
     */
    boolean isChecked(Long memberId,  Date checkinDate);
}