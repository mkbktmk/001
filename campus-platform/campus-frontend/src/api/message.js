import request from './request'

export function sendMessage(data) {
  return request.post('/second-hand/message', data)
}

export function getConversations() {
  return request.get('/second-hand/message/conversations')
}

export function getMessages(goodsId, withUserId) {
  return request.get('/second-hand/message/list', { params: { goodsId, withUserId } })
}

export function getUnreadCount() {
  return request.get('/second-hand/message/unread')
}
