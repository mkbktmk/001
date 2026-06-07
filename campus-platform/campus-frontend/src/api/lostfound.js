import request from './request'

export function getLostFoundList(params) {
  return request.get('/lost-found/list', { params })
}

export function getLostFoundDetail(id) {
  return request.get(`/lost-found/detail/${id}`)
}

export function createLostFound(data) {
  return request.post('/lost-found', data)
}

export function updateLostFound(id, data) {
  return request.put(`/lost-found/${id}`, data)
}

export function deleteLostFound(id) {
  return request.delete(`/lost-found/${id}`)
}

export function getMyLostFound(params) {
  return request.get('/lost-found/my', { params })
}

export function markFound(id) {
  return request.put(`/lost-found/found/${id}`)
}
