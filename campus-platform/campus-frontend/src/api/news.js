import request from './request'

export function getNewsList(params) {
  return request.get('/news/list', { params })
}

export function getNewsDetail(id) {
  return request.get(`/news/detail/${id}`)
}

export function publishNews(data) {
  return request.post('/news/publish', data)
}

export function updateNews(id, data) {
  return request.put(`/news/${id}`, data)
}

export function offlineNews(id) {
  return request.put(`/news/offline/${id}`)
}
