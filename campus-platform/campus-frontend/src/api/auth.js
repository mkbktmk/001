import request from './request'

export function login(data) {
  return request.post('/auth/login', data)
}

export function register(data) {
  return request.post('/auth/register', data)
}

export function getCaptcha() {
  return request.get('/auth/captcha')
}

export function refreshToken() {
  return request.post('/auth/refresh')
}

export function getMyInfo() {
  return request.get('/user/me')
}

export function updateMyInfo(data) {
  return request.put('/user/me', data)
}
