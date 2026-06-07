<template>
  <div class="page-container">
    <van-nav-bar title="编辑资料" left-arrow @click-left="$router.back()" />

    <van-form @submit="handleSubmit" class="form">
      <!-- 头像 -->
      <div class="avatar-section" @click="triggerAvatarInput">
        <input ref="avatarInputRef" type="file" accept="image/*" style="display:none" @change="onAvatarChange" />
        <div class="avatar-wrapper">
          <van-image v-if="avatarUrl" round width="80" height="80" :src="avatarUrl" fit="cover">
            <template #loading><van-icon name="photograph" size="40" color="#ccc" /></template>
          </van-image>
          <van-icon v-else name="user-circle-o" size="80" color="#ccc" />
          <div class="avatar-tip">{{ avatarUrl ? '更换头像' : '点击上传头像' }}</div>
        </div>
      </div>

      <van-cell-group inset>
        <van-field v-model="form.nickname" label="昵称" placeholder="你的昵称" :rules="[{ required: true }]" />
        <van-field v-model="form.phone" label="手机号" placeholder="请输入手机号" />
        <van-field v-model="form.email" label="邮箱" placeholder="请输入邮箱" />
        <van-field v-model="form.college" label="学院" placeholder="所在学院" />
        <van-field v-model="form.major" label="专业" placeholder="所学专业" />
        <van-field v-model="form.grade" label="年级" placeholder="如 2023" />
      </van-cell-group>

      <div class="btn">
        <van-button round block type="primary" native-type="submit" :loading="loading">保存</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyInfo, updateMyInfo } from '../api/auth'
import { uploadImages } from '../api/goods'
import { useUserStore } from '../stores/user'
import { showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const avatarUrl = ref('')
const avatarInputRef = ref(null)

function triggerAvatarInput() {
  avatarInputRef.value?.click()
}

const form = reactive({
  nickname: '', phone: '', email: '',
  college: '', major: '', grade: ''
})

onMounted(async () => {
  try {
    const res = await getMyInfo()
    const u = res.data
    form.nickname = u.nickname || ''
    form.phone = u.phone || ''
    form.email = u.email || ''
    form.college = u.college || ''
    form.major = u.major || ''
    form.grade = u.grade || ''
    avatarUrl.value = u.avatarUrl || ''
  } catch {}
})

const avatarUploading = ref(false)

async function onAvatarChange(e) {
  const file = e.target.files?.[0]
  if (!file) return
  if (!file.type.startsWith('image/')) { showToast('请选择图片文件'); return }
  if (file.size > 5 * 1024 * 1024) { showToast('头像不能超过5MB'); return }
  avatarUploading.value = true
  try {
    const res = await uploadImages([file])
    if (res.code === 200 && res.data.length > 0) {
      avatarUrl.value = res.data[0]
      showToast('头像上传成功')
    } else {
      showToast(res.message || '上传失败')
    }
  } catch (e) { showToast(e?.message || '上传超时，请重试') }
  finally { avatarUploading.value = false }
}

async function handleSubmit() {
  loading.value = true
  try {
    const data = { ...form }
    if (avatarUrl.value) data.avatarUrl = avatarUrl.value
    const res = await updateMyInfo(data)
    userStore.userInfo = { ...userStore.userInfo, ...res.data }
    localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
    setTimeout(() => router.back(), 800)
  } catch {} finally { loading.value = false }
}
</script>

<style scoped>
.form { margin-top: 0; }
.avatar-section {
  display: flex;
  justify-content: center;
  padding: 24px 0;
  background: #fff;
  margin-bottom: 12px;
}
.avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}
.avatar-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}
.btn { margin: 24px 16px; }
</style>
