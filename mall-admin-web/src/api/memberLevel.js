import request from '@/utils/request'

// 获取会员等级列表
export function fetchList(params) {
  return request({
    url: '/memberLevel/list',
    method: 'get',
    params: params
  })
}

// 创建会员等级
export function createMemberLevel(data) {
  return request({
    url: '/memberLevel/create',
    method: 'post',
    data: data
  })
}

// 批量删除会员等级
export function deleteMemberLevel(ids) {
  return request({
    url: '/memberLevel/delete',
    method: 'delete',
    params: {
      ids: Array.isArray(ids) ? ids.join(',') : ids
    }
  })
}

// 获取所有会员等级用于下拉框选项
export function fetchMemberLevelList() {
  return request({
    url: '/memberLevel/list',
    method: 'get',
    params: {
      pageSize: 100, // 获取足够多的数据
      pageNum: 1
    }
  })
}

// 更新会员等级
export function updateMemberLevel(id, data) {
  return request({
    url: `/memberLevel/${id}`,
    method: 'put',
    data: data
  })
}
