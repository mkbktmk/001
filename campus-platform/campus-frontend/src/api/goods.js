import request from './request'

// 图片上传（用 request 实例，自动带 token，不手动设 Content-Type）
export function uploadImages(files) {
  const formData = new FormData()
  files.forEach(f => formData.append('files', f))
  return request.post('/second-hand/upload', formData)
}

export function getGoodsList(params) {
  return request.get('/second-hand/list', { params })
}

export function getGoodsDetail(id) {
  return request.get(`/second-hand/detail/${id}`)
}

export function createGoods(data) {
  return request.post('/second-hand', data)
}

export function updateGoods(id, data) {
  return request.put(`/second-hand/${id}`, data)
}

export function changeGoodsStatus(id, status) {
  return request.put(`/second-hand/status/${id}`, null, { params: { status } })
}

export function toggleGoodsFavorite(id) {
  return request.post(`/second-hand/favorite/${id}`)
}

export function isGoodsFavorited(id) {
  return request.get(`/second-hand/favorite/${id}/status`)
}

export function getMyFavorites() {
  return request.get('/second-hand/favorites')
}

export function getMyGoodsList(params) {
  return request.get('/second-hand/my-list', { params })
}
