<template>
  <view class="score-mall-container">
    <view class="header">
      <text class="title">积分商城</text>
      <text class="desc">1积分可抵0.01元，兑换心仪好物</text>
    </view>
    <view class="product-list">
      <view class="product-item" v-for="item in productList" :key="item.id" @click="goDetail(item)">
        <view class="image-wrapper">
          <image :src="getProductImg(item)" mode="aspectFill" class="product-img" />
        </view>
        <view class="product-info">
          <text class="product-name">{{ item.name }}</text>
          <text class="product-desc">{{ item.subTitle }}</text>
          <view class="product-meta">
            <text class="product-score">{{ item.price * 100 }} 积分</text>
            <text class="product-price">市场价：￥{{ item.originalPrice }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { fetchScoreMallProductList } from '@/api/scoreMall.js'
export default {
  data() {
    return {
      productList: []
    }
  },
  created() {
    this.loadScoreProducts()
  },
  methods: {
    loadScoreProducts() {
      fetchScoreMallProductList({}).then(res => {
        this.productList = Array.isArray(res.data) ? res.data : (res.data.list || [])
      })
    },
    getProductImg(item) {
      // 优先使用pic字段，否则取albumPics第一张，否则用默认图片
      if (item.pic) return item.pic;
      if (item.albumPics) {
        if (item.albumPics.indexOf(',') > -1) {
          return item.albumPics.split(',')[0];
        } else {
          return item.albumPics;
        }
      }
      return '/static/errorImage.jpg';
    },
    goDetail(item) {
      uni.navigateTo({
        url: '/pages/scoreMall/scoreProductDetail?id=' + item.id
      })
    }
  }
}
</script>

<style lang="scss" scoped>
page,
.score-mall-container {
  background: $page-color-base;
}
.score-mall-container {
  min-height: 100vh;
}
.header {
  text-align: center;
  margin-bottom: 30rpx;
}
.title {
  font-size: 40rpx;
  font-weight: bold;
  color: #fa436a;
}
.desc {
  font-size: 28rpx;
  color: #666;
  margin-top: 10rpx;
}
.product-list {
  display: flex;
  flex-wrap: wrap;
  padding: 0 30upx 30upx 30upx;
  background: #fff;

  .product-item {
    width: 48%;
    min-width: 320rpx;
    max-width: 400rpx;
    background: #fff;
    border-radius: 20rpx;
    box-shadow: 0 4rpx 24rpx rgba(0,0,0,0.08);
    padding: 28rpx 18rpx 22rpx 18rpx;
    margin-bottom: 28rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    transition: box-shadow 0.2s;
    &:nth-child(2n+1) {
      margin-right: 4%;
    }
    &:hover {
      box-shadow: 0 10rpx 32rpx rgba(250,67,106,0.14);
    }
  }

  .image-wrapper {
    width: 90%;
    height: 260rpx;
    border-radius: 12rpx;
    overflow: hidden;
    background-color: #f6f6f6;
    margin-bottom: 18rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    .product-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      opacity: 1;
      display: block;
      border-radius: 12rpx;
      box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
    }
  }

  .product-info {
    text-align: center;
    width: 100%;
    padding: 0 4rpx;
  }
  .product-name {
    font-size: 34rpx;
    color: #222;
    line-height: 44rpx;
    font-weight: 600;
    margin-bottom: 6rpx;
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .product-desc {
    font-size: 24rpx;
    color: #888;
    line-height: 34rpx;
    height: 68rpx;
    margin-bottom: 8rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    display: block;
  }
  .product-meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 8rpx;
    font-size: 26rpx;
    color: #fa436a;
    margin-top: 10rpx;
  }
  .product-score {
    font-size: 30rpx;
    color: #fa436a;
    font-weight: bold;
  }
  .product-price {
    font-size: 24rpx;
    color: #999;
    font-weight: 500;
  }
}
</style>
