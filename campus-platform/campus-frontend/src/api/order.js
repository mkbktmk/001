import request from './request'

export function createOrder(data) {
  return request.post('/second-hand/order', data)
}

export function getMyOrders() {
  return request.get('/second-hand/order/my')
}
