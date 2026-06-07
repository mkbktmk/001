<template>
  <div class="page-container">
    <van-nav-bar title="资讯详情" left-arrow @click-left="$router.back()" />
    <div v-if="detail" class="detail">
      <img v-if="detail.coverImage" :src="detail.coverImage" class="cover" alt="" />
      <h1>{{ detail.title }}</h1>
      <div class="meta">
        {{ detail.authorName }} · {{ formatTime(detail.publishTime) }} · {{ detail.viewCount }} 阅读
      </div>
      <div class="content" v-html="detail.content"></div>

      <!-- 作者/管理员操作 -->
      <div v-if="canManage" class="manage-bar">
        <van-button type="primary" size="small" @click="$router.push(`/news-edit/${detail.id}`)">编辑</van-button>
        <van-button v-if="detail.status === 'published'" type="danger" size="small" @click="handleOffline" style="margin-left:8px">下架</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNewsDetail, offlineNews } from '../api/news'
import { useUserStore } from '../stores/user'
import { showConfirmDialog, showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const detail = ref(null)

const canManage = computed(() => {
  if (!userStore.isLoggedIn() || !detail.value) return false
  return userStore.userInfo?.role === 'admin' ||
    userStore.userInfo?.userId === detail.value.authorId
})

function formatTime(t) {
  return t ? new Date(t).toLocaleDateString('zh-CN') : ''
}

async function handleOffline() {
  await showConfirmDialog({ title: '确认下架', message: '下架后将不再公开展示' })
  try {
    await offlineNews(detail.value.id)
    detail.value.status = 'offline'
    showToast('已下架')
  } catch {}
}

onMounted(async () => {
  try {
    const res = await getNewsDetail(route.params.id)
    detail.value = res.data
  } catch {}
})
</script>

<style scoped>
.detail { background: #fff; padding: 0 0 20px; min-height: 100vh; }
.cover { width: 100%; max-height: 220px; object-fit: cover; }
.detail h1 { font-size: 22px; line-height: 1.4; padding: 16px 20px 0; }
.meta { font-size: 12px; color: #999; margin: 12px 20px 20px; }
.content { font-size: 15px; white-space: pre-wrap; word-break: break-word; line-height: 1.8; padding: 0 20px; }
.content :deep(img) { max-width: 100%; }
.manage-bar { padding: 20px; display: flex; gap: 8px; }
</style>
