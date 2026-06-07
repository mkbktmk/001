import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getMyInfo } from '../api/auth'
import { showToast } from 'vant'
import router from '../router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = () => !!token.value

  async function login(username, password) {
    const res = await loginApi({ username, password })
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    // 保存用户信息
    const info = {
      userId: res.data.userId,
      username: res.data.username,
      nickname: res.data.nickname,
      role: res.data.role,
      avatarUrl: res.data.avatarUrl
    }
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
    showToast('登录成功')
    try {
      await router.push('/home')
    } catch (e) {
      console.error('路由跳转失败:', e)
      // 降级：直接修改 hash
      window.location.hash = '#/home'
    }
  }

  async function fetchUserInfo() {
    try {
      const res = await getMyInfo()
      userInfo.value = res.data
      localStorage.setItem('userInfo', JSON.stringify(res.data))
    } catch (e) {
      logout()
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }

  return { token, userInfo, isLoggedIn, login, fetchUserInfo, logout }
})
