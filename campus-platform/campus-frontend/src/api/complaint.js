import request from './request'

export function getMyComplaints(params) {
  return request.get('/complaint/my', { params })
}

export function submitComplaint(data) {
  return request.post('/complaint', data)
}

export function rateComplaint(id, rating, feedback) {
  return request.put(`/complaint/rate/${id}`, null, { params: { rating, feedback } })
}

// 管理员接口
export function getAdminComplaints(params) {
  return request.get('/complaint/admin/list', { params })
}

export function handleComplaint(id, status, reply) {
  return request.put(`/complaint/admin/handle/${id}`, null, { params: { status, reply } })
}
