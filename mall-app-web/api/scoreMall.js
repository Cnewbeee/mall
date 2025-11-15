import request from '@/utils/requestUtil'

// 获取积分商城商品列表
// 获取积分商城商品列表（新版接口，分页）
export function fetchScoreMallProductList(params) {
  return request({
    method: 'GET',
    url: '/home/integrationProductList',
    params
  })
}

// 获取积分商品详情
export function fetchScoreMallProductDetail(id) {
  return request({
    method: 'GET',
    url: '/scoreMall/productDetail/' + id
  })
}
