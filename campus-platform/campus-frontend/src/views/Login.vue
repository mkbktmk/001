<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-avatar">
        <span>🎓</span>
      </div>
      <h1>校园综合服务平台</h1>
      <p class="subtitle">登录你的账号，开启校园生活</p>

      <van-form @submit="handleLogin">
        <van-cell-group inset>
          <van-field v-model="form.username" left-icon="user-o" placeholder="学号 / 工号"
            :rules="[{ required: true, message: '请输入用户名' }]" />
          <van-field v-model="form.password" type="password" left-icon="lock" placeholder="密码"
            :rules="[{ required: true, message: '请输入密码' }]" />
        </van-cell-group>

        <div class="btn-wrap">
          <van-button round block type="primary" native-type="submit" :loading="loading" size="large">登录</van-button>
        </div>
      </van-form>

      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>

      <div class="foot">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useUserStore } from '../stores/user'
const userStore = useUserStore()
const loading = ref(false)
const errorMsg = ref('')
const form = reactive({ username: '', password: '' })
async function handleLogin() {
  errorMsg.value = ''
  if (!form.username || !form.password) { errorMsg.value = '请输入用户名和密码'; return }
  loading.value = true
  try { await userStore.login(form.username, form.password) }
  catch (e) { errorMsg.value = e?.message || '账号或密码错误' }
  finally { loading.value = false }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(160deg, #667eea 0%, #764ba2 100%);
  display: flex; align-items: center; justify-content: center;
  padding: 32px 20px;
}
.login-card {
  width: 100%; max-width: 380px;
  background: #fff; border-radius: 16px;
  padding: 40px 24px 28px;
  box-shadow: 0 12px 40px rgba(0,0,0,0.12);
}
.login-avatar {
  width: 72px; height: 72px; border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto 16px;
}
.login-avatar span { font-size: 36px; }
.login-card h1 { text-align: center; font-size: 20px; font-weight: 600; color: #333; margin: 0 0 6px; }
.subtitle { text-align: center; font-size: 13px; color: #999; margin: 0 0 24px; }

.btn-wrap { margin: 24px 16px 0; }
.btn-wrap :deep(.van-button--large) { height: 46px; font-size: 16px; font-weight: 600; }

.error { margin: 12px 16px 0; font-size: 13px; color: #ff4d4f; text-align: center; }

.foot { text-align: center; margin-top: 20px; font-size: 13px; color: #999; }
.foot a { color: #667eea; text-decoration: none; font-weight: 500; }
</style>
