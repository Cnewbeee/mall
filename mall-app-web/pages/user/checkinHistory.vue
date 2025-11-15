<template>
    <view class="container">
        <view class="top-bar">
            <view class="header-section">
                <view class="header-content">
                    <view class="stats-section">
                        <view class="tj-item">
                            <text class="yticon icon-calendar"
                                style="font-size:48rpx;color:#fa436a;margin-right:10rpx;"></text>
                            <text class="num">{{ continuousDays }}</text>
                            <text style="margin-left:8rpx;">连续签到</text>
                        </view>
                        <view class="tj-item">
                            <text class="yticon icon-list"
                                style="font-size:48rpx;color:#6ad6fa;margin-right:10rpx;"></text>
                            <text class="num">{{ totalCheckins }}</text>
                            <text style="margin-left:8rpx;">累计签到</text>
                        </view>
                    </view>
                    <view style="margin-top:18rpx;text-align:center;color:#fff;font-size:24rpx;opacity:0.85;">
                        坚持签到，积分多多，奖励多多！
                    </view>
                </view>
            </view>
            <view class="view-toggle">
                <view class="toggle-item" :class="{ active: viewType === 'list' }" @click="switchView('list')">
                    列表视图
                </view>
                <view class="toggle-item" :class="{ active: viewType === 'calendar' }" @click="switchView('calendar')">
                    日历视图
                </view>
            </view>
        </view>
        <view class="main-content">
            <view class="calendar-section" v-show="viewType === 'calendar'">
                <view class="calendar-header">
                    <view class="month-nav" @click="prevMonth">
                        <text class="yticon icon-zuo"></text>
                    </view>
                    <view class="month-title">
                        {{ currentYear }}年{{ currentMonth }}月
                    </view>
                    <view class="month-nav" @click="nextMonth">
                        <text class="yticon icon-you"></text>
                    </view>
                </view>
                <view class="week-header">
                    <view class="week-item" v-for="week in weekDays" :key="week">{{ week }}</view>
                </view>
                <view class="calendar-grid">
                    <view class="calendar-day empty" v-for="n in firstDayOfWeek" :key="'empty-' + n"></view>
                    <view class="calendar-day" v-for="day in daysInMonth" :key="day" :class="{
                        'today': isToday(currentYear, currentMonth, day),
                        'checked': isDateChecked(currentYear, currentMonth, day),
                        'missed': isDateMissed(currentYear, currentMonth, day),
                        'future': isFutureDate(currentYear, currentMonth, day)
                    }" @click="handleDayClick(currentYear, currentMonth, day)">
                        <view class="day-number">{{ day }}</view>
                        <view class="day-indicator" v-if="isDateChecked(currentYear, currentMonth, day)"></view>
                        <view class="day-indicator missed" v-else-if="isDateMissed(currentYear, currentMonth, day)">
                        </view>
                    </view>
                </view>
                <view class="calendar-legend">
                    <view class="legend-item">
                        <view class="legend-color checked"></view>
                        <text class="legend-text">已签到</text>
                    </view>
                    <view class="legend-item">
                        <view class="legend-color missed"></view>
                        <text class="legend-text">未签到</text>
                    </view>
                    <view class="legend-item">
                        <view class="legend-color today"></view>
                        <text class="legend-text">今日</text>
                    </view>
                </view>
            </view>
            <view class="history-section" v-show="viewType === 'list'">
                <view class="section-header">
                    <text class="section-title">签到明细</text>
                    <text class="section-count">共 {{ totalCheckins }} 条</text>
                </view>
                <scroll-view scroll-y class="history-scroll" @scrolltolower="loadMoreData">
                    <view class="history-list">
                        <view class="history-item" v-for="item in checkinList" :key="item.id"
                            :class="{ 'today': isTodayStr(item.checkinDate) }">
                            <view class="item-left">
                                <view class="date-info">
                                    <text class="date">{{ formatDate(item.checkinDate) }}</text>
                                    <text class="week">{{ getWeekDay(item.checkinDate) }}</text>
                                </view>
                                <view class="checkin-badge"
                                    v-if="isTodayStr(item.checkinDate) || item.isRecheckin === 1">
                                    <text class="badge-text" v-if="isTodayStr(item.checkinDate)">今日</text>
                                    <text class="badge-text" v-else-if="item.isRecheckin === 1">补签</text>
                                </view>
                            </view>
                            <view class="item-right">
                                <text class="integration-num">{{ item.integration < 0 ? '' : '+' }}{{ item.integration }}</text>
                                        <text class="integration-label">积分</text>
                            </view>
                        </view>
                    </view>
                    <view class="load-more-container" v-if="loading || hasMore">
                        <uni-load-more :status="loadStatus" :content-text="loadMoreText"></uni-load-more>
                    </view>
                    <view class="empty-state" v-if="checkinList.length === 0 && !loading">
                        <view class="empty-icon">📋</view>
                        <text class="empty-title">暂无签到记录</text>
                        <text class="empty-desc">开始您的签到之旅，赚取积分吧！</text>
                    </view>
                </scroll-view>
            </view>
        </view>
        <view class="recheckin-modal" v-if="showRecheckinModal">
            <view class="modal-overlay" @click="closeRecheckinModal"></view>
            <view class="modal-content">
                <view class="modal-header">
                    <text class="modal-title">补签确认</text>
                    <text class="modal-close" @click="closeRecheckinModal">×</text>
                </view>
                <view class="modal-body">
                    <text class="modal-text">确定要补签 {{ recheckinDate }} 吗？</text>
                    <text class="modal-subtext">补签将消耗一定积分</text>
                </view>
                <view class="modal-footer">
                    <button class="modal-btn cancel" @click="closeRecheckinModal">取消</button>
                    <button class="modal-btn confirm" @click="confirmRecheckin">确认补签</button>
                </view>
            </view>
        </view>
    </view>
</template>

<script>
import {
    fetchMemberCheckinHistory,
    fetchMemberContinuousDays,
    fetchMemberCheckinDetail,
    fetchMemberRecheckin
} from '@/api/checkin.js'
import uniLoadMore from '@/components/uni-load-more/uni-load-more.vue'

export default {
    components: {
        uniLoadMore
    },
    data() {
        return {
            viewType: 'calendar', // 'list' 或 'calendar'
            checkinList: [],
            continuousDays: 0,
            totalCheckins: 0,
            pageNum: 1,
            pageSize: 15,
            hasMore: true,
            loading: false,
            loadStatus: 'more',
            loadMoreText: {
                contentdown: '上拉加载更多',
                contentrefresh: '正在加载...',
                contentnomore: '没有更多了'
            },

            // 日历相关数据
            currentYear: new Date().getFullYear(),
            currentMonth: new Date().getMonth() + 1,
            weekDays: ['日', '一', '二', '三', '四', '五', '六'],
            checkinDetail: [], // 当前月份的签到详情

            // 补签相关
            showRecheckinModal: false,
            recheckinDate: '',
            recheckinDateObj: null
        }
    },
    computed: {
        // 当月第一天是星期几 (0-6, 0表示星期日)
        firstDayOfWeek() {
            return new Date(this.currentYear, this.currentMonth - 1, 1).getDay();
        },
        // 当月总天数
        daysInMonth() {
            return new Date(this.currentYear, this.currentMonth, 0).getDate();
        }
    },
    watch: {
        // 当切换月份时重新加载数据
        currentYear() {
            this.loadCheckinDetail();
        },
        currentMonth() {
            this.loadCheckinDetail();
        }
    },
    onLoad() {
        this.loadData();
        this.loadCheckinDetail();
    },
    onPullDownRefresh() {
        this.refreshData();
    },
    methods: {
        // 切换视图
        switchView(view) {
            this.viewType = view;
        },

        // 切换到上一个月
        prevMonth() {
            if (this.currentMonth === 1) {
                this.currentYear--;
                this.currentMonth = 12;
            } else {
                this.currentMonth--;
            }
        },

        // 切换到下一个月
        nextMonth() {
            if (this.currentMonth === 12) {
                this.currentYear++;
                this.currentMonth = 1;
            } else {
                this.currentMonth++;
            }
        },

        // 加载数据
        loadData() {
            this.loading = true;
            this.loadStatus = 'loading';

            return Promise.all([
                this.loadCheckinHistory(),
                this.loadContinuousDays()
            ]).finally(() => {
                this.loading = false;
                this.loadStatus = 'more';
            });
        },

        // 刷新数据
        refreshData() {
            this.pageNum = 1;
            this.checkinList = [];
            this.hasMore = true;
            this.loadData().then(() => {
                uni.stopPullDownRefresh();
                uni.showToast({
                    title: '刷新成功',
                    icon: 'success',
                    duration: 1000
                });
            });
        },

        // 加载更多
        loadMoreData() {
            if (!this.hasMore || this.loading) return;

            this.pageNum++;
            this.loading = true;
            this.loadStatus = 'loading';

            this.loadCheckinHistory().finally(() => {
                this.loading = false;
                this.loadStatus = this.hasMore ? 'more' : 'noMore';
            });
        },

        // 获取签到历史
        loadCheckinHistory() {
            return fetchMemberCheckinHistory({
                pageNum: this.pageNum,
                pageSize: this.pageSize
            }).then(response => {
                console.log('签到历史响应数据:', response);

                if (response.code === 200 && response.data) {
                    const data = response.data;
                    const list = data.list || [];

                    if (this.pageNum === 1) {
                        this.checkinList = list;
                    } else {
                        this.checkinList = [...this.checkinList, ...list];
                    }

                    this.totalCheckins = data.total || 0;
                    this.hasMore = this.pageNum * this.pageSize < (data.total || 0);

                    if (!this.hasMore) {
                        this.loadStatus = 'noMore';
                    }
                } else {
                    console.error('响应格式错误:', response);
                }
            }).catch(error => {
                console.error('加载签到历史失败:', error);
                uni.showToast({
                    title: '加载失败，请重试',
                    icon: 'none'
                });
            });
        },

        // 获取连续签到天数
        loadContinuousDays() {
            return fetchMemberContinuousDays().then(response => {
                console.log('连续签到天数响应:', response);

                if (response.code === 200 && response.data) {
                    this.continuousDays = response.data.continuousDays || 0;
                    console.log('连续签到天数:', this.continuousDays);
                }
            }).catch(error => {
                console.error('加载连续签到天数失败:', error);
            });
        },

        // 获取指定月份的签到详情
        loadCheckinDetail() {
            return fetchMemberCheckinDetail({
                year: this.currentYear,
                month: this.currentMonth
            }).then(response => {
                if (response.code === 200 && response.data) {
                    // 确保 checkinDetail 是一个数组
                    if (Array.isArray(response.data)) {
                        this.checkinDetail = response.data;
                    } else if (Array.isArray(response.data.list)) {
                        // 如果返回的是分页对象，使用其中的 list 字段
                        this.checkinDetail = response.data.list;
                    } else {
                        // 兜底处理，确保始终是数组
                        this.checkinDetail = [];
                    }
                } else {
                    // 请求失败时也确保是数组
                    this.checkinDetail = [];
                }
            }).catch(error => {
                console.error('加载签到详情失败:', error);
                // 出错时也确保是数组
                this.checkinDetail = [];
            });
        },

        // 判断是否为今天
        isToday(year, month, day) {
            const today = new Date();
            return (
                year === today.getFullYear() &&
                month === today.getMonth() + 1 &&
                day === today.getDate()
            );
        },

        // 判断是否为今天 (字符串版本)
        isTodayStr(dateStr) {
            try {
                const date = new Date(dateStr);
                const today = new Date();
                return date.toDateString() === today.toDateString();
            } catch (error) {
                console.error('日期处理错误:', error);
                return false;
            }
        },

        // 判断指定日期是否已签到
        isDateChecked(year, month, day) {
            const dateStr = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
            return this.checkinDetail.some(item => {
                // 处理不同的日期格式
                if (item.checkinDate) {
                    // 如果是新格式 (如: 2025-10-28 00:00:00)
                    if (item.checkinDate.includes(' ')) {
                        const datePart = item.checkinDate.split(' ')[0];
                        return datePart === dateStr;
                    }
                    // 如果是完整日期格式 (如: 2025-10-26T16:00:00.000+00:00)
                    if (item.checkinDate.includes('T')) {
                        const datePart = item.checkinDate.split('T')[0];
                        return datePart === dateStr;
                    }
                    // 如果是简单日期格式 (如: 2025-10-26)
                    return item.checkinDate === dateStr;
                }
                return false;
            });
        },

        // 判断指定日期是否未签到 (过去的日期但未签到)
        isDateMissed(year, month, day) {
            // 只有过去的日期才可能显示为未签到
            if (this.isFutureDate(year, month, day)) {
                return false;
            }

            // 今天及以前的日期，如果没有签到则显示为未签到
            const dateStr = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
            const isSigned = this.checkinDetail.some(item => {
                // 处理不同的日期格式
                if (item.checkinDate) {
                    // 如果是新格式 (如: 2025-10-28 00:00:00)
                    if (item.checkinDate.includes(' ')) {
                        const datePart = item.checkinDate.split(' ')[0];
                        return datePart === dateStr;
                    }
                    // 如果是完整日期格式 (如: 2025-10-26T16:00:00.000+00:00)
                    if (item.checkinDate.includes('T')) {
                        const datePart = item.checkinDate.split('T')[0];
                        return datePart === dateStr;
                    }
                    // 如果是简单日期格式 (如: 2025-10-26)
                    return item.checkinDate === dateStr;
                }
                return false;
            });

            // 如果是今天且未签到，也显示为未签到
            if (this.isToday(year, month, day)) {
                return !isSigned;
            }

            // 如果是过去的日期且未签到，显示为未签到
            const checkDate = new Date(year, month - 1, day);
            const today = new Date();
            today.setHours(0, 0, 0, 0);

            return checkDate < today && !isSigned;
        },


        // 判断是否为未来的日期
        isFutureDate(year, month, day) {
            const checkDate = new Date(year, month - 1, day);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            return checkDate > today;
        },

        // 处理日期点击事件
        handleDayClick(year, month, day) {
            // 未来日期不能点击
            if (this.isFutureDate(year, month, day)) {
                return;
            }

            // 已签到的日期不能补签
            if (this.isDateChecked(year, month, day)) {
                uni.showToast({
                    title: '该日期已签到',
                    icon: 'none'
                });
                return;
            }

            // 今天的签到应该走正常签到流程
            if (this.isToday(year, month, day)) {
                // 这里可以触发正常的签到流程
                this.$emit('today-checkin');
                return;
            }

            // 显示补签确认弹窗
            this.recheckinDateObj = new Date(year, month - 1, day);
            this.recheckinDate = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
            this.showRecheckinModal = true;
        },

        // 关闭补签弹窗
        closeRecheckinModal() {
            this.showRecheckinModal = false;
            this.recheckinDate = '';
            this.recheckinDateObj = null;
        },

        // 确认补签
        confirmRecheckin() {
            if (!this.recheckinDate) {
                return;
            }

            uni.showLoading({
                title: '补签中...'
            });

            fetchMemberRecheckin({ targetDate: this.recheckinDate }).then(response => {
                if (response.code === 200) {
                    uni.showToast({
                        title: '补签成功',
                        icon: 'success',
                        duration: 2000
                    });

                    // 更新用户信息
                    this.$store.commit('UPDATE_USER_INFO', {
                        ...this.userInfo,
                        integration: response.data.remainingIntegration,
                    });

                    // 关闭弹窗
                    this.closeRecheckinModal();

                    // 重新加载数据
                    this.loadCheckinDetail();
                    this.loadContinuousDays();
                } else {
                    uni.showToast({
                        title: response.message || '补签失败',
                        icon: 'none',
                        duration: 2000
                    });
                }
            }).catch(error => {
                console.error('补签失败:', error);
                uni.showToast({
                    title: response.message || '补签失败',
                    icon: 'none',
                    duration: 2000
                });
            }).finally(() => {
                // 无论成功还是失败都关闭弹窗
                this.closeRecheckinModal();
                uni.hideLoading();
            });
        },

        // 格式化日期
        formatDate(dateStr) {
            try {
                const date = new Date(dateStr);
                const month = date.getMonth() + 1;
                const day = date.getDate();
                return `${month}月${day}日`;
            } catch (error) {
                console.error('日期格式化错误:', error);
                return '日期错误';
            }
        },

        // 获取星期
        getWeekDay(dateStr) {
            try {
                const date = new Date(dateStr);
                const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
                return weekDays[date.getDay()];
            } catch (error) {
                console.error('星期获取错误:', error);
                return '未知';
            }
        }
    }
}
</script>

<style lang="scss" scoped>
%flex-center {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

%section {
    display: flex;
    justify-content: space-around;
    align-content: center;
    background: #fff;
    border-radius: 10upx;
}

.container {
    min-height: 100vh;
    background: #f5f5f5;
}

.header-section {
    background: linear-gradient(270deg, #fa436a, #5663f3, #bef957, #6ad6fa, #fa436a);
    background-size: 600% 600%;
    animation: gradientMove 8s ease infinite;
    padding: 40rpx 30rpx;
    border-bottom: 1px solid #f0f0f0;
}

@keyframes gradientMove {
    0% {
        background-position: 0% 50%;
    }

    25% {
        background-position: 50% 50%;
    }

    50% {
        background-position: 100% 50%;
    }

    75% {
        background-position: 50% 50%;
    }

    100% {
        background-position: 0% 50%;
    }
}

.header-content {
    text-align: center;
}

.header-title {
    margin-bottom: 40rpx;

    .title-text {
        display: block;
        font-size: 36rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 10rpx;
    }

    .title-sub {
        display: block;
        font-size: 26rpx;
        color: #999;
    }
}

// 视图切换
.view-toggle {
    display: flex;
    background: #fff;
    margin: 20rpx;
    border-radius: 10rpx;
    overflow: hidden;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.toggle-item {
    flex: 1;
    text-align: center;
    padding: 20rpx 0;
    font-size: 28rpx;
    color: #666;
    transition: all 0.3s;

    &.active {
        background: #fa436a;
        color: #fff;
        font-weight: bold;
    }
}

// 日历区域
.calendar-section {
    background: #fff;
    margin: 0 20rpx 20rpx;
    border-radius: 10rpx;
    padding: 30rpx;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;

    .month-nav {
        width: 60rpx;
        height: 60rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        background: #f5f5f5;
        font-size: 28rpx;
        color: #666;

        &:active {
            background: #eee;
        }
    }

    .month-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
    }
}

.week-header {
    display: flex;
    margin-bottom: 20rpx;

    .week-item {
        flex: 1;
        text-align: center;
        font-size: 24rpx;
        color: #999;
        padding: 15rpx 0;
    }
}

.calendar-grid {
    display: flex;
    flex-wrap: wrap;
}

.calendar-day {
    width: 14.2857%;
    aspect-ratio: 1/1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;
    margin-bottom: 10rpx;

    .day-number {
        font-size: 28rpx;
        color: #333;
        z-index: 1;
    }

    .day-indicator {
        position: absolute;
        bottom: 8rpx;
        width: 12rpx;
        height: 12rpx;
        border-radius: 50%;
        background: #fa436a;

        &.missed {
            background: #ccc;
        }
    }

    &.today {
        .day-number {
            color: #fa436a;
            font-weight: bold;
        }

        /* 修改今天的样式：默认红色边框白色填充 */
        &::before {
            content: '';
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 60rpx;
            height: 60rpx;
            border-radius: 50%;
            border: 2rpx solid #fa436a;
            background: #fff;
            z-index: 0;
        }
    }

    &.checked {
        .day-number {
            color: #fa436a;
            font-weight: bold;
        }
    }

    &.missed {
        .day-number {
            color: #ccc;
        }
    }

    &.future {
        .day-number {
            color: #ccc;
        }
    }

    &.empty {
        visibility: hidden;
    }

    &:active:not(.empty):not(.future) {
        background: rgba(250, 67, 106, 0.1);
        border-radius: 10rpx;
    }
}

.calendar-legend {
    display: flex;
    justify-content: center;
    margin-top: 30rpx;
    padding-top: 20rpx;
    border-top: 1rpx solid #f0f0f0;

    .legend-item {
        display: flex;
        align-items: center;
        margin: 0 20rpx;

        .legend-color {
            width: 20rpx;
            height: 20rpx;
            border-radius: 50%;
            margin-right: 10rpx;

            &.checked {
                background: #fa436a;
            }

            &.missed {
                background: #ccc;
            }

            &.today {
                background: #fa436a;
                position: relative;

                &::after {
                    content: '';
                    position: absolute;
                    top: -2rpx;
                    right: -2rpx;
                    width: 10rpx;
                    height: 10rpx;
                    border-radius: 50%;
                    background: #fff;
                    border: 2rpx solid #fa436a;
                }
            }
        }

        .legend-text {
            font-size: 24rpx;
            color: #666;
        }
    }
}

// 历史记录区域
.history-section {
    margin: 20rpx;
    background: #fff;
    border-radius: 10rpx;
    padding: 30rpx;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;
    padding-bottom: 20rpx;
    border-bottom: 1px solid #f0f0f0;

    .section-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
    }

    .section-count {
        font-size: 24rpx;
        color: #999;
    }
}

.history-scroll {
    max-height: 800rpx;
}

.history-list {
    .history-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 30rpx 20rpx;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
            border-bottom: none;
        }

        &:active {
            background: #fafafa;
        }

        &.today {
            background: #fff9f0;
            border-radius: 8rpx;
            margin: 10rpx 0;
            padding: 30rpx 20rpx;
        }
    }
}

.item-left {
    display: flex;
    align-items: center;
    gap: 20rpx;

    .date-info {
        display: flex;
        flex-direction: column;

        .date {
            font-size: 28rpx;
            font-weight: 500;
            color: #333;
            margin-bottom: 8rpx;
        }

        .week {
            font-size: 24rpx;
            color: #999;
        }
    }

    .checkin-badge {
        background: #ff6b6b;
        padding: 6rpx 16rpx;
        border-radius: 20rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        min-height: 40rpx;

        .badge-text {
            font-size: 20rpx;
            color: #fff;
            font-weight: bold;
            line-height: 1;
        }
    }

}

.item-right {
    text-align: right;

    .integration-num {
        font-size: 32rpx;
        font-weight: bold;
        color: #ff6b6b;
        margin-bottom: 8rpx;
        display: block;
    }

    .integration-label {
        font-size: 24rpx;
        color: #999;
    }
}

// 加载更多
.load-more-container {
    padding: 30rpx 0;
    text-align: center;
}

// 空状态
.empty-state {
    text-align: center;
    padding: 100rpx 0;

    .empty-icon {
        font-size: 120rpx;
        margin-bottom: 30rpx;
        display: block;
        opacity: 0.5;
    }

    .empty-title {
        font-size: 32rpx;
        color: #666;
        margin-bottom: 20rpx;
        display: block;
    }

    .empty-desc {
        font-size: 28rpx;
        color: #ccc;
        display: block;
    }
}

// 补签弹窗
.recheckin-modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 9999;
}

.modal-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
}

.modal-content {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 80%;
    background: #fff;
    border-radius: 20rpx;
    overflow: hidden;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15rpx;
    border-bottom: 1rpx solid #f0f0f0;

    .modal-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
    }

    .modal-close {
        font-size: 40rpx;
        color: #999;
        padding: 10rpx;
    }
}

.modal-body {
    padding: 40rpx 30rpx;
    text-align: center;

    .modal-text {
        font-size: 30rpx;
        color: #333;
        display: block;
        margin-bottom: 20rpx;
    }

    .modal-subtext {
        font-size: 26rpx;
        color: #999;
    }
}

.modal-footer {
    display: flex;
    border-top: 1rpx solid #f0f0f0;

    .modal-btn {
        flex: 1;
        font-size: 30rpx;
        border: none;
        background: none;

        &.cancel {
            color: #666;
            border-right: 1rpx solid #f0f0f0;
        }

        &.confirm {
            color: #fa436a;
            font-weight: bold;
        }

        &:active {
            background: #f5f5f5;
        }
    }
}
</style>
