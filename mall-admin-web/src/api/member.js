import request from '@/utils/request'
// 获取会员列表，支持分字段查询
export function fetchList(params) {
  return request({
    url: '/member/list',
    method: 'get',
    params: {
      ...params,
      username: params.username,
      nickname: params.nickname,
      phone: params.phone
    }
  })
}
export function createMember(data) {
  return request({
    url: '/member/create',
    method: 'post',
    data: data
  })
}

// 更新会员信息
export function updateMember(id, data) {
  return request({
    url: `/member/update/${id}`,
    method: 'post',
    data: data
  })
}

// 批量删除会员
export function deleteMember(ids) {
  return request({
    url: '/member/delete',
    method: 'post',
    params: {
      ids: Array.isArray(ids) ? ids.join(',') : ids
    }
  })
}


// 修改会员状态
export function updateStatus(id, data) {
  return request({
    url: `/member/updateStatus/${id}`,
    method: 'post',
    data: data
  })
}

// 获取会员积分变化历史记录
export function fetchIntegrationHistory(memberId, params) {
  return request({
    url: `/member/historyIntegration/${memberId}`,
    method: 'get',
    params: params
  });
}
