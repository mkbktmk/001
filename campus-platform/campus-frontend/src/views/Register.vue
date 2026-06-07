<template>
  <div class="register-page">
    <van-nav-bar title="注册" left-arrow @click-left="$router.back()" />

    <van-form @submit="handleRegister" class="form">
      <van-cell-group inset>
        <van-field v-model="form.username" label="学号/工号" placeholder="请输入学号或工号"
          :rules="[{ required: true }]" />
        <van-field v-model="form.password" type="password" label="密码" placeholder="6-32位密码"
          :rules="[{ required: true, min: 6 }]" />
        <van-field v-model="form.nickname" label="昵称" placeholder="怎么称呼你？"
          :rules="[{ required: true }]" />
        <van-field v-model="form.phone" label="手机号" placeholder="选填" />
        <van-field v-model="form.email" label="邮箱" placeholder="选填" />
        <van-field name="role" label="身份">
          <template #input>
            <van-radio-group v-model="form.role" direction="horizontal">
              <van-radio name="student">学生</van-radio>
              <van-radio name="teacher">教师</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field v-model="form.college" label="学院" placeholder="选填" />
        <van-field v-model="form.major" label="专业" placeholder="选填" />
        <van-field v-model="form.grade" label="年级" placeholder="如: 2023" />

        <!-- 图形验证码 -->
        <van-field v-model="form.captchaCode" label="验证码" placeholder="请输入验证码"
          :rules="[{ required: true, message: '请输入验证码' }]">
          <template #button>
            <img v-if="captchaImage" :src="captchaImage" class="captcha-img" @click="refreshCaptcha" title="点击刷新" />
          </template>
        </van-field>
      </van-cell-group>

      <div class="btn">
        <van-button round block type="primary" native-type="submit" :loading="loading">
          注 册
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { register, getCaptcha } from '../api/auth'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const captchaKey = ref('')
const captchaImage = ref('')

const form = reactive({
  username: '', password: '', nickname: '',
  phone: '', email: '', role: 'student',
  college: '', major: '', grade: '',
  captchaCode: ''
})

async function refreshCaptcha() {
  try {
    const res = await getCaptcha()
    captchaKey.value = res.data.key
    captchaImage.value = res.data.image
  } catch {}
}

onMounted(() => refreshCaptcha())

async function handleRegister() {
  loading.value = true
  try {
    await register({ ...form, captchaKey: captchaKey.value })
    showToast('注册成功，快去登录吧')
    setTimeout(() => router.push('/login'), 1000)
  } catch (e) {
    showToast(e?.message || '注册失败')
    refreshCaptcha()
    form.captchaCode = ''
  } finally { loading.value = false }
}
</script>

<style scoped>
.register-page { min-height: 100vh; background: var(--campus-bg); }
.form { margin-top: 12px; }
.btn   { margin: 24px 16px; }
.captcha-img { height: 44px; cursor: pointer; border-radius: 4px; }
</style>
