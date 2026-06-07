import axios from 'axios'
import { showToast } from 'vant'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 防止并发刷新
let isRefreshing = false
let refreshSubscribers = []

function onRefreshed(newToken) {
  refreshSubscribers.forEach(cb => cb(newToken))
  refreshSubscribers = []
}

function subscribeTokenRefresh(cb) {
  refreshSubscribers.push(cb)
}

// 请求拦截器 — 附加 Token
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
}, error => Promise.reject(error))

// 响应拦截器 — 统一错误处理 + Token 自动刷新
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      // 登录/注册页面自行处理错误展示，不弹 toast
      const skipToast = response.config.url.includes('/auth/login') || response.config.url.includes('/auth/register')
      if (!skipToast) {
        showToast(res.message || '请求失败')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  async error => {
    const originalRequest = error.config
    if (error.response?.status === 401 && !originalRequest._retry) {
      // 排除刷新接口自身和登录接口
      const url = originalRequest.url
      if (url === '/auth/refresh' || url === '/auth/login') {
        localStorage.removeItem('token')
        window.location.hash = '#/login'
        return Promise.reject(error)
      }

      if (isRefreshing) {
        // 等待刷新完成
        return new Promise(resolve => {
          subscribeTokenRefresh(token => {
            originalRequest.headers['Authorization'] = `Bearer ${token}`
            originalRequest._retry = true
            resolve(service(originalRequest))
          })
        })
      }

      originalRequest._retry = true
      isRefreshing = true

      try {
        const res = await service.post('/auth/refresh')
        const newToken = res.data.token
        localStorage.setItem('token', newToken)
        isRefreshing = false
        onRefreshed(newToken)
        originalRequest.headers['Authorization'] = `Bearer ${newToken}`
        return service(originalRequest)
      } catch (refreshError) {
        isRefreshing = false
        refreshSubscribers = []
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        window.location.hash = '#/login'
        showToast('登录已过期，请重新登录')
        return Promise.reject(refreshError)
      }
    }

    if (!error.response) {
      showToast('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default service
