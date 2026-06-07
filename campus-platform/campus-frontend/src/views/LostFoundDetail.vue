<template>
  <div class="page-container">
    <van-nav-bar title="启事详情" left-arrow @click-left="$router.back()" />
    <div v-if="item" class="detail">
      <div class="header-tag">
        <van-tag :type="item.type === 'lost' ? 'danger' : 'success'" size="large">
          {{ item.type === 'lost' ? '🔍 寻物启事' : '📦 招领启事' }}
        </van-tag>
        <van-tag v-if="item.status === 'found'" type="primary" size="large" style="margin-left:8px">已找到</van-tag>
      </div>

      <h2>{{ item.itemName }}</h2>
      <div class="meta">
        <span>{{ item.category }}</span>
        <span>· {{ item.userName }}</span>
        <span>· {{ formatTime(item.createTime) }}</span>
        <span>· 👁 {{ item.viewCount }}</span>
      </div>

      <div class="info-row">
        <span>📍 {{ item.location || '未填写' }}</span>
        <span>📞 {{ item.contact || '未填写' }}</span>
      </div>

      <div v-if="imageList.length > 0" class="detail-images">
        <img v-for="(url, i) in imageList" :key="i" :src="url" class="detail-img" />
      </div>

      <div class="desc">{{ item.description }}</div>

      <div v-if="isOwner" class="btn-row">
        <template v-if="item.status === 'active'">
          <van-button type="primary" round @click="handleEdit">编辑</van-button>
          <van-button type="success" round @click="handleMarkFound" style="margin-left:8px">标记已找到</van-button>
        </template>
        <van-button type="danger" round @click="handleDelete" style="margin-left:8px">删除</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLostFoundDetail, markFound, deleteLostFound } from '../api/lostfound'
import { useUserStore } from '../stores/user'
import { showConfirmDialog, showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const item = ref(null)

const imageList = computed(() => {
  try {
    const imgs = item.value?.images; if (!imgs) return []
    const parsed = typeof imgs === 'string' ? JSON.parse(imgs) : imgs
    return Array.isArray(parsed) ? parsed : []
  } catch { return [] }
})

const isOwner = computed(() => {
  return userStore.userInfo?.userId && item.value?.userId &&
    userStore.userInfo.userId === item.value.userId
})

function formatTime(t) {
  return t ? new Date(t).toLocaleDateString('zh-CN') : ''
}

function handleEdit() {
  router.push(`/lostfound-edit/${item.value.id}`)
}

async function handleMarkFound() {
  await showConfirmDialog({ title: '确认', message: '确认已找到/已归还？' })
  try {
    await markFound(item.value.id)
    item.value.status = 'found'
    showToast('状态已更新')
  } catch {}
}

async function handleDelete() {
  await showConfirmDialog({ title: '确认删除', message: '删除后不可恢复' })
  try {
    await deleteLostFound(item.value.id)
    showToast('已删除')
    setTimeout(() => router.back(), 800)
  } catch {}
}

onMounted(async () => {
  try {
    const res = await getLostFoundDetail(route.params.id)
    item.value = res.data
  } catch {}
})
</script>

<style scoped>
.detail { background: #fff; padding: 20px; min-height: 100vh; }
.header-tag { margin-bottom: 16px; }
.detail h2 { font-size: 22px; margin-bottom: 10px; }
.meta { font-size: 12px; color: #999; margin-bottom: 16px; }
.info-row { display: flex; gap: 24px; font-size: 14px; color: #666; margin-bottom: 16px; padding: 12px; background: #f7f8fa; border-radius: 8px; }
.desc { font-size: 15px; line-height: 1.8; color: #333; }
.btn-row { margin-top: 32px; display: flex; flex-wrap: wrap; gap: 8px; }
.detail-images { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 16px; }
.detail-img { width: 100px; height: 100px; object-fit: cover; border-radius: 6px; cursor: pointer; }
</style>
