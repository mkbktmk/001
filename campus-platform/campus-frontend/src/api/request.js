import axios from 'axios'

const service = axios.create({ baseURL: '/api', timeout: 10000 })

let isRefreshing = false, refreshSubscribers = []
function onRefreshed(t) { refreshSubscribers.forEach(cb => cb(t)); refreshSubscribers = [] }
function subscribeTokenRefresh(cb) { refreshSubscribers.push(cb) }

service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers['Authorization'] = 'Bearer ' + token
  return config
}, error => Promise.reject(error))

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) return Promise.reject(new Error(res.message || '请求失败'))
    return res
  },
  async error => {
    const originalRequest = error.config
    if (error.response?.status === 401 && !originalRequest._retry) {
      const url = originalRequest.url
      if (url === '/auth/refresh' || url === '/auth/login') {
        localStorage.removeItem('token'); window.location.hash = '#/login'
        return Promise.reject(error)
      }
      if (isRefreshing) {
        return new Promise(resolve => {
          subscribeTokenRefresh(token => {
            originalRequest.headers['Authorization'] = 'Bearer ' + token
            originalRequest._retry = true; resolve(service(originalRequest))
          })
        })
      }
      originalRequest._retry = true; isRefreshing = true
      try {
        const res = await service.post('/auth/refresh')
        const newToken = res.data.token
        localStorage.setItem('token', newToken)
        isRefreshing = false; onRefreshed(newToken)
        originalRequest.headers['Authorization'] = 'Bearer ' + newToken
        return service(originalRequest)
      } catch (refreshError) {
        isRefreshing = false; refreshSubscribers = []
        localStorage.removeItem('token'); localStorage.removeItem('userInfo')
        window.location.hash = '#/login'
        return Promise.reject(refreshError)
      }
    }
    return Promise.reject(error)
  }
)

export default service
