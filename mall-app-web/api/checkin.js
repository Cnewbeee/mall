import request from '@/utils/requestUtil'

/**
 * 会员签到
 */
export function fetchMemberCheckin() {
    return request({
        url: '/member/checkin',
        method: 'post'
    })
}

/**
 * 获取签到记录
 * @param {Object} params 查询参数
 */
export function fetchMemberCheckinHistory(params) {
    return request({
        url: '/member/checkin/history',
        method: 'get',
        params
    })
}

/**
 * 获取连续签到天数
 */
export function fetchMemberContinuousDays() {
    return request({
        url: '/member/checkin/continuous',
        method: 'get'
    })
}

/**
 * 检查今日是否已签到
 */
export function fetchCheckToday() {
    return request({
        url: '/member/checkin/today',
        method: 'get'
    })
}

/**
 * 获取指定月份的签到详情
 */
export function fetchMemberCheckinDetail(params) {
    return request({
        url: '/member/checkin/detail',
        method: 'get',
        params: params
    })
}

export function fetchMemberRecheckin(params) {
    return request({
        url: '/member/checkin/retroactive',
        method: 'post',
        params: params
    })
}