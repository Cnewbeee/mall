<template>
	<view class="container">
		<view class="checkin-modal" v-if="showCheckinModal" :class="{ 'show': modalVisible }">
			<view class="modal-overlay" @click="closeModal"></view>
			<view class="modal-content" :class="{ 'animate': modalAnimating }">
				<view class="success-icon">🎉</view>
				<view class="success-title">签到成功</view>
				<view class="success-rewards">
					<view class="reward-item">
						<text class="reward-label">获得积分：</text>
						<text class="reward-value">+{{ checkinReward.integration }}</text>
					</view>
					<view class="reward-item">
						<text class="reward-label">成长值：</text>
						<text class="reward-value">+{{ checkinReward.growthValue }}</text>
					</view>
					<view class="reward-item" v-if="checkinReward.continuousDays > 1">
						<text class="reward-label">连续签到：</text>
						<text class="reward-value">{{ checkinReward.continuousDays }}天</text>
					</view>
				</view>
				<button class="modal-close-btn" @click="closeModal">确定</button>
			</view>
		</view>

		<view class="user-section dynamic-bg">
			<view class="user-info-box">
				<view class="portrait-box">
					<image class="portrait" :src="userInfo.icon || '/static/missing-face.png'"></image>
				</view>
				<view class="info-box">
					<text class="username">{{ userInfo.nickname || '游客' }}</text>
				</view>
			</view>
			<view class="vip-card-box" :style="{ transform: `scale(${vipCardScale})` }">
				<image class="card-bg" src="/static/vip-card-bg.png" mode=""></image>
				<view class="b-btn">
					立即开通
				</view>
				<view class="tit">
					<text class="yticon icon-iLinkapp-"></text>
					黄金会员
				</view>
				<text class="e-m">mall移动端商城</text>
				<text class="e-b">黄金及以上会员可享有会员价优惠商品。</text>
			</view>
		</view>

		<view class="cover-container" :style="[{
			transform: coverTransform,
			transition: coverTransition
		}]" @touchstart="coverTouchstart" @touchmove="coverTouchmove" @touchend="coverTouchend">
			<image class="arc" src="/static/arc.png"></image>

			<view class="tj-sction">
				<view class="tj-item">
					<text class="num">{{ userInfo.integration || '暂无' }}</text>
					<text>积分</text>
				</view>
				<view class="tj-item">
					<text class="num">{{ userInfo.growth || '暂无' }}</text>
					<text>成长值</text>
				</view>
				<view class="tj-item" @click="navTo('/pages/coupon/couponList')">
					<text class="num">{{ couponCount || '暂无' }}</text>
					<text>优惠券</text>
				</view>
			</view>
			<!-- 单独签到按钮 -->
			<view class="checkin-btn-section">
				<button class="checkin-btn" :disabled="checkedToday" @click="handleCheckin">
					<text v-if="!checkedToday">签到</text>
					<text v-else>今日已签</text>
				</button>
			</view>
			<!-- 订单 -->
			<view class="order-section">
				<view class="order-item" @click="navTo('/pages/order/order?state=0')" hover-class="common-hover"
					:hover-stay-time="50">
					<text class="yticon icon-shouye"></text>
					<text>全部订单</text>
				</view>
				<view class="order-item" @click="navTo('/pages/order/order?state=1')" hover-class="common-hover"
					:hover-stay-time="50">
					<text class="yticon icon-daifukuan"></text>
					<text>待付款</text>
				</view>
				<view class="order-item" @click="navTo('/pages/order/order?state=2')" hover-class="common-hover"
					:hover-stay-time="50">
					<text class="yticon icon-yishouhuo"></text>
					<text>待收货</text>
				</view>
				<view class="order-item" hover-class="common-hover" :hover-stay-time="50">
					<text class="yticon icon-shouhoutuikuan"></text>
					<text>退款/售后</text>
				</view>
			</view>
			<!-- 浏览历史 -->
			<view class="history-section icon">
				<scroll-view scroll-x class="history-horizontal-list">
					<view class="horizontal-list">

						<view class="horizontal-item" @click="navTo('/pages/user/readHistory')">
							<text class="icon yticon icon-lishijilu" :style="{ color: '#e07472' }"></text>
							<text class="item-title">我的足迹</text>
						</view>
						<view class="horizontal-item" @click="navTo('/pages/user/brandAttention')">
							<text class="icon yticon icon-shoucang" :style="{ color: '#5fcda2' }"></text>
							<text class="item-title">我的关注</text>
						</view>
						<view class="horizontal-item" @click="navTo('/pages/user/productCollection')">
							<text class="icon yticon icon-shoucang_xuanzhongzhuangtai"
								:style="{ color: '#54b4ef' }"></text>
							<text class="item-title">我的收藏</text>
						</view>
						<view class="horizontal-item">
							<text class="icon yticon icon-pingjia" :style="{ color: '#ee883b' }"></text>
							<text class="item-title">我的评价</text>
						</view>
					</view>
				</scroll-view>
			</view>
			<view class="history-section icon">
				<!-- 纵向排列区块 -->
				<view class="vertical-list">
					<view class="vertical-item" @click="navTo('/pages/user/checkinHistory')">
						<text class="icon yticon icon-naozhong" :style="{ color: '#e07472' }"></text>
						<text class="item-title">签到记录</text>
					</view>
					<view class="vertical-item" @click="navTo('/pages/scoreMall/scoreMall')">
						<text class="icon yticon icon-daifukuan" :style="{ color: '#ff736a' }"></text>
						<text class="item-title">积分商城</text>
					</view>
					<view class="vertical-item" @click="navTo('/pages/address/address')">
						<text class="icon yticon icon-dizhi" :style="{ color: '#5fcda2' }"></text>
						<text class="item-title">地址管理</text>
					</view>

					<view class="vertical-item" @click="navTo('/pages/set/set')">
						<text class="icon yticon icon-shezhi1" :style="{ color: '#e07472' }"></text>
						<text class="item-title">设置</text>
					</view>
				</view>
			</view>
		</view>


	</view>
</template>
<script>
import listCell from '@/components/mix-list-cell';
import {
	fetchMemberCouponList
} from '@/api/coupon.js';
import {
	fetchMemberCheckin,
	fetchMemberContinuousDays,
	fetchCheckToday
} from '@/api/checkin.js';
import {
	mapState
} from 'vuex';
let startY = 0, moveY = 0, pageAtTop = true;
export default {
	components: {
		listCell
	},
	data() {
		return {
			coverTransform: 'translateY(0px)',
			coverTransition: '0s',
			moving: false,
			couponCount: null,
			checkedToday: false,
			continuousDays: 0,
			isAnimating: false,
			showCheckinModal: false,
			modalVisible: false,
			modalAnimating: false,
			checkinReward: {
				integration: 0,
				growthValue: 0,
				continuousDays: 0
			},
			vipCardScale: 1
		}
	},
	onLoad() {
	},
	onShow() {
		if (this.hasLogin) {
			fetchMemberCouponList(0).then(response => {
				if (response.data != null && response.data.length > 0) {
					this.couponCount = response.data.length;
				}
			});
			this.loadCheckinStatus();
		} else {
			this.couponCount = null;
			this.checkedToday = false;
			this.continuousDays = 0;
		}
	},
	computed: {
		...mapState(['hasLogin', 'userInfo'])
	},
	// #ifndef MP
	onNavigationBarButtonTap(e) {
		const index = e.index;
		if (index === 0) {
			// 点击第一个按钮（消息）
			this.navTo('/pages/set/set');
		} else if (index === 1) {
			// 点击第二个按钮（设置）
			this.navTo('/pages/notice/notice');
		}
	},
	// #endif
	methods: {
		/**
		 * 统一跳转接口,拦截未登录路由
		 */
		navTo(url) {
			if (!this.hasLogin) {
				url = '/pages/public/login';
			}
			uni.navigateTo({
				url
			})
		},

		/**
		 * 加载签到状态
		 */
		loadCheckinStatus() {
			fetchCheckToday().then(response => {
				this.checkedToday = response.data.checkedToday;
			});
			fetchMemberContinuousDays().then(response => {
				this.continuousDays = response.data.continuousDays;
			});
		},

		/**
		 * 显示签到成功弹窗
		 */
		showCheckinSuccessModal(reward) {
			this.checkinReward = reward;
			this.showCheckinModal = true;

			// 延迟显示动画，确保DOM渲染完成
			this.$nextTick(() => {
				setTimeout(() => {
					this.modalVisible = true;
					this.modalAnimating = true;
				}, 50);
			});
		},

		/**
		 * 关闭签到弹窗
		 */
		closeModal() {
			this.modalVisible = false;
			this.modalAnimating = false;

			setTimeout(() => {
				this.showCheckinModal = false;
			}, 300);
		},

		/**
		 * 处理签到
		 */
		handleCheckin() {
			if (!this.hasLogin) {
				this.navTo('/pages/public/login');
				return;
			}
			if (this.checkedToday) {
				uni.showToast({
					title: '今日已签到',
					icon: 'none'
				});
				return;
			}

			fetchMemberCheckin().then(response => {
				if (response.code == 200) {
					this.checkedToday = true;
					// 立即刷新连续签到天数
					this.continuousDays = response.data.continuousDays;

					// 更新用户信息
					this.$store.commit('UPDATE_USER_INFO', {
						...this.userInfo,
						integration: this.userInfo.integration + response.data.integration,
						// growthValue: (this.userInfo.growthValue || 0) + (response.data.growthValue || 10)
					});

					// 显示签到成功弹窗
					this.showCheckinSuccessModal({
						integration: response.data.integration,
						growthValue: response.data.growthValue || 10,
						continuousDays: response.data.continuousDays
					});
				} else {
					uni.showToast({
						title: response.message || '签到失败',
						icon: 'none'
					});
				}
			}).catch(error => {
				uni.showToast({
					title: '签到失败',
					icon: 'none'
				});
			});
		},

		/**
		 * 会员卡下拉和回弹
		 */
		coverTouchstart(e) {
			if (pageAtTop === false) {
				return;
			}
			this.coverTransition = 'transform .1s linear';
			startY = e.touches[0].clientY;
		},
		coverTouchmove(e) {
			moveY = e.touches[0].clientY;
			let moveDistance = moveY - startY;
			if (moveDistance < 0) {
				this.moving = false;
				return;
			}
			this.moving = true;
			if (moveDistance >= 80 && moveDistance < 100) {
				moveDistance = 80;
			}

			if (moveDistance > 0 && moveDistance <= 80) {
				this.coverTransform = `translateY(${moveDistance}px)`;
			}
		},
		coverTouchend() {
			if (this.moving === false) {
				return;
			}
			this.moving = false;
			this.coverTransition = 'transform 0.3s cubic-bezier(.21,1.93,.53,.64)';
			this.coverTransform = 'translateY(0px)';
		}
	}
}  
</script>
<style lang='scss'>
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

.user-section {
	height: 520upx;
	padding: 100upx 30upx 0;
	position: relative;

	.bg {
		position: absolute;
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;
		filter: blur(1px);
		opacity: .7;
	}
}

.user-info-box {
	height: 180upx;
	display: flex;
	align-items: center;
	position: relative;
	z-index: 1;

	.portrait {
		width: 130upx;
		height: 130upx;
		border: 5upx solid #fff;
		border-radius: 50%;
	}

	.username {
		font-size: $font-lg + 6upx;
		color: $font-color-dark;
		margin-left: 20upx;
	}
}

.vip-card-box {
	display: flex;
	flex-direction: column;
	color: #f7d680;
	height: 240upx;
	background: linear-gradient(to left, rgba(0, 0, 0, .7), rgba(0, 0, 0, .8));
	border-radius: 16upx 16upx 0 0;
	overflow: hidden;
	position: relative;
	padding: 20upx 24upx;

	.card-bg {
		position: absolute;
		top: 20upx;
		right: 0;
		width: 380upx;
		height: 260upx;
	}

	.b-btn {
		position: absolute;
		right: 20upx;
		top: 22upx;
		width: 132upx;
		height: 40upx;
		text-align: center;
		line-height: 40upx;
		font-size: 22upx;
		color: #36343c;
		border-radius: 20px;
		background: linear-gradient(to left, #f9e6af, #ffd465);
		z-index: 1;
	}

	.tit {
		font-size: $font-base+2upx;
		color: #f7d680;
		margin-bottom: 28upx;

		.yticon {
			color: #f6e5a3;
			margin-right: 16upx;
		}
	}

	.e-b {
		font-size: $font-sm;
		color: #d8cba9;
		margin-top: 10upx;
	}
}

.cover-container {
	background: $page-color-base;
	margin-top: -150upx;
	padding: 0 30upx;
	position: relative;
	background: #f5f5f5;
	padding-bottom: 20upx;

	.arc {
		position: absolute;
		left: 0;
		top: -34upx;
		width: 100%;
		height: 36upx;
	}
}

.tj-sction {
	@extend %section;

	.tj-item {
		@extend %flex-center;
		flex-direction: column;
		height: 140upx;
		font-size: $font-sm;
		color: #75787d;
		position: relative;
		transition: all 0.3s ease;
	}

	.tj-item.checkin-item {
		cursor: pointer;
	}

	.tj-item.checkin-item.checked {
		background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
		border-radius: 10upx;
	}

	.tj-item.checkin-item.animating {
		transform: scale(1.05);
		box-shadow: 0 4upx 20upx rgba(250, 67, 106, 0.3);
	}

	.num {
		font-size: $font-lg;
		color: $font-color-dark;
		margin-bottom: 8upx;
	}

	.checkin-tip {
		font-size: 20upx;
		color: #fa436a;
		margin-top: 8upx;
	}

	.checkin-tip.checked {
		color: #999;
	}

	.checkin-icon {
		position: absolute;
		top: 10upx;
		right: 10upx;
		width: 40upx;
		height: 40upx;
		border-radius: 50%;
		background: #fa436a;
		display: flex;
		align-items: center;
		justify-content: center;
		color: white;
		font-size: 20upx;
		transition: all 0.3s ease;
	}

	.checkin-icon.pulse {
		animation: pulse 1s infinite;
	}

	@keyframes pulse {
		0% {
			transform: translate3d(0, 0, 0);
		}

		20% {
			transform: translate3d(0, -4upx, 0);
		}

		40%,
		53% {
			transform: translate3d(0, 0, 0);
		}

		70% {
			transform: translate3d(0, -15upx, 0);
		}

		80%,
		100% {
			transform: translate3d(0, 0, 0);
		}
	}
}

.order-section {
	@extend %section;
	padding: 28upx 0;
	margin-top: 20upx;

	.order-item {
		@extend %flex-center;
		width: 120upx;
		height: 120upx;
		border-radius: 10upx;
		font-size: $font-sm;
		color: $font-color-dark;
	}

	.yticon {
		font-size: 48upx;
		margin-bottom: 18upx;
		color: #fa436a;
	}

	.icon-shouhoutuikuan {
		font-size: 44upx;
	}
}

.history-section {
	// padding: 30upx 0;
	margin-top: 20upx;
	background: #fff;
	border-radius: 10upx;

	.sec-header {
		display: flex;
		align-items: center;
		font-size: $font-base;
		color: $font-color-dark;
		line-height: 40upx;
		margin-left: 30upx;

		.yticon {
			font-size: 44upx;
			color: #5eba8f;
			margin-right: 16upx;
			line-height: 40upx;
		}
	}

	.h-list {
		white-space: nowrap;
		padding: 30upx 30upx 0;

		image {
			display: inline-block;
			width: 160upx;
			height: 160upx;
			margin-right: 20upx;
			border-radius: 10upx;
		}
	}
}

/* 签到成功弹窗样式 */
.checkin-modal {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	z-index: 9999;
	display: none;

	&.show {
		display: block;
	}
}

.modal-overlay {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.5);
	opacity: 0;
	transition: opacity 0.3s ease;

	.show & {
		opacity: 1;
	}
}

.modal-content {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%) scale(0.8);
	background: #fff;
	border-radius: 20upx;
	padding: 60upx 40upx;
	width: 80%;
	max-width: 600upx;
	text-align: center;
	opacity: 0;
	transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);

	&.animate {
		transform: translate(-50%, -50%) scale(1);
		opacity: 1;
	}
}

.success-icon {
	font-size: 120upx;
	margin-bottom: 30upx;
	display: block;
	animation: bounce 0.6s ease;
}

.success-title {
	font-size: 40upx;
	font-weight: bold;
	color: #333;
	margin-bottom: 40upx;
}

.success-rewards {
	margin-bottom: 40upx;
}

.reward-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20upx 0;
	border-bottom: 1upx solid #f0f0f0;

	&:last-child {
		border-bottom: none;
	}
}

.reward-label {
	font-size: 28upx;
	color: #666;
}

.reward-value {
	font-size: 32upx;
	font-weight: bold;
	color: #fa436a;
}

.modal-close-btn {
	background: linear-gradient(90deg, #fa436a 0%, #f6e5a3 100%);
	color: #fff;
	border: none;
	border-radius: 30upx;
	font-size: 30upx;
	transition: all 0.3s ease;

	&:active {
		transform: scale(0.95);
	}
}

@keyframes bounce {

	0%,
	20%,
	53%,
	80%,
	100% {
		transform: translate3d(0, 0, 0);
	}

	40%,
	43% {
		transform: translate3d(0, -30upx, 0);
	}

	70% {
		transform: translate3d(0, -15upx, 0);
	}

	90% {
		transform: translate3d(0, -4upx, 0);
	}
}

/* 签到按钮样式 */
.checkin-btn-section {
	display: flex;
	justify-content: center;
	width: 100%;
	margin: 20upx 0 0 0;
}

.checkin-btn {
	background: linear-gradient(90deg, #fa436a 0%, #89b3f6 70%);
	color: #fff;
	width: 100%;
	font-size: 32upx;
	border: none;
	border-radius: 30upx;
	padding: 0 60upx;
	height: 70upx;
	line-height: 70upx;
	box-shadow: 0 4upx 20upx rgba(250, 67, 106, 0.15);
	transition: background 0.3s;
}

.checkin-btn[disabled] {
	background: linear-gradient(90deg, #38c0bc 0%, #d4a2e0 100%) !important;
	color: #fff;
}


.history-horizontal-list {
	width: 100%;
	white-space: nowrap;
	overflow-x: auto;
	scrollbar-width: thin;
	scrollbar-color: #fa436a #f5f5f5;
}

.horizontal-list {
	display: flex;
	flex-direction: row;
	align-items: stretch;
	justify-content: space-between;
	width: 100%;
}

.horizontal-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	flex: 1;
	min-width: 0;
	margin: 30upx 0;
}

.horizontal-item .icon {
	font-size: 50upx;
	margin-bottom: 10upx;
}

.vertical-list {
	display: flex;
	flex-direction: column;
	align-items: stretch;
	margin: 0 30upx 20upx 30upx;
	gap: 10upx;
}

.vertical-item {
	display: flex;
	flex-direction: row;
	align-items: center;
	padding: 24upx 0;
	border-bottom: 1px solid #f0f0f0;
	cursor: pointer;
}

.vertical-item:last-child {
	border-bottom: none;
}

.vertical-item .icon {
	font-size: 48upx;
	margin-right: 24upx;
	margin-bottom: 0;
}

.vertical-item .item-title {
	font-size: 28upx;
	color: #333;
	text-align: left;
}

.dynamic-bg {
	position: relative;
	background-color: #e493d0;
	background-image:
		radial-gradient(closest-side, rgba(235, 105, 78, 1), rgba(235, 105, 78, 0)),
		radial-gradient(closest-side, rgb(11, 239, 243), rgba(243, 11, 164, 0)),
		radial-gradient(closest-side, rgb(68, 89, 244), rgba(254, 234, 131, 0)),
		radial-gradient(closest-side, rgba(170, 142, 245, 1), rgba(170, 142, 245, 0)),
		radial-gradient(closest-side, rgba(248, 192, 147, 1), rgba(248, 192, 147, 0));
	background-size:
		130vmax 130vmax,
		80vmax 80vmax,
		90vmax 90vmax,
		110vmax 110vmax,
		90vmax 90vmax;
	background-position:
		-80vmax -80vmax,
		60vmax -30vmax,
		10vmax 10vmax,
		-30vmax -10vmax,
		50vmax 50vmax;
	background-repeat: no-repeat;
	animation: user-bg-movement 8s linear infinite;
}

@keyframes user-bg-movement {

	0%,
	100% {
		background-size:
			130vmax 130vmax,
			80vmax 80vmax,
			90vmax 90vmax,
			110vmax 110vmax,
			90vmax 90vmax;
		background-position:
			-80vmax -80vmax,
			60vmax -30vmax,
			10vmax 10vmax,
			-30vmax -10vmax,
			50vmax 50vmax;
	}

	25% {
		background-size:
			100vmax 100vmax,
			90vmax 90vmax,
			100vmax 100vmax,
			90vmax 90vmax,
			60vmax 60vmax;
		background-position:
			80vmax -60vmax,
			-50vmax 40vmax,
			0vmax 60vmax,
			40vmax 20vmax,
			-40vmax 60vmax;
	}

	50% {
		background-size:
			80vmax 80vmax,
			110vmax 110vmax,
			80vmax 80vmax,
			60vmax 60vmax,
			80vmax 80vmax;
		background-position:
			50vmax 70vmax,
			-40vmax -30vmax,
			-10vmax 0vmax,
			20vmax -10vmax,
			-30vmax -70vmax;
	}

	75% {
		background-size:
			90vmax 90vmax,
			90vmax 90vmax,
			100vmax 100vmax,
			90vmax 90vmax,
			70vmax 70vmax;
		background-position:
			-50vmax 40vmax,
			50vmax 30vmax,
			-20vmax 0vmax,
			10vmax -10vmax,
			-40vmax -60vmax;
	}
}
</style>
