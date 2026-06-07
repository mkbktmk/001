import request from './request'

export function getNotifications(params) {
  return request.get('/notification/list', { params })
}

export function getUnreadCount() {
  return request.get('/notification/unread')
}

export function markRead(id) {
  return request.put(`/notification/read/${id}`)
}
