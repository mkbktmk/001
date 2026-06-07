import request from './request'
import axios from 'axios'

// 上传专用实例，更长超时
const uploadService = axios.create({
  baseURL: '/api',
  timeout: 60000
})
uploadService.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers['Authorization'] = 'Bearer ' + token
  return config
})

export function uploadImages(files) {
  const formData = new FormData()
  files.forEach(f => formData.append('files', f))
  return uploadService.post('/second-hand/upload', formData).then(r => r.data)
}

export function getGoodsList(params) { return request.get('/second-hand/list', { params }) }
export function getGoodsDetail(id) { return request.get('/second-hand/detail/' + id) }
export function createGoods(data) { return request.post('/second-hand', data) }
export function updateGoods(id, data) { return request.put('/second-hand/' + id, data) }
export function changeGoodsStatus(id, status) { return request.put('/second-hand/status/' + id, null, { params: { status } }) }
export function toggleGoodsFavorite(id) { return request.post('/second-hand/favorite/' + id) }
export function isGoodsFavorited(id) { return request.get('/second-hand/favorite/' + id + '/status') }
export function getMyFavorites() { return request.get('/second-hand/favorites') }
export function getMyGoodsList(params) { return request.get('/second-hand/my-list', { params }) }
