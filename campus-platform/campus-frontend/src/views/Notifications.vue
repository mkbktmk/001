<template>
  <div class="page-container">
    <van-nav-bar title="消息中心" left-arrow @click-left="$router.back()" fixed placeholder />

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" @load="onLoad">
        <div v-for="n in list" :key="n.id" class="msg-item" :class="{ unread: n.isRead === 0 }"
          @click="handleClick(n)">
          <div class="msg-title">
            <span v-if="n.isRead === 0" class="dot"></span>
            {{ n.title }}
          </div>
          <div class="msg-content">{{ n.content }}</div>
          <div class="msg-time">{{ formatTime(n.createTime) }}</div>
        </div>
        <van-empty v-if="!loading && list.length === 0" description="暂无消息" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNotifications, markRead } from '../api/notification'

const router = useRouter()
const list = ref([])
const page = ref(1)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const diff = now - d
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return d.toLocaleDateString('zh-CN')
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getNotifications({ page: page.value, size: 15 })
    const records = res.data?.records || []
    if (page.value === 1) list.value = records
    else list.value.push(...records)
    finished.value = records.length < 15
  } catch {} finally { loading.value = false; refreshing.value = false }
}

async function handleClick(n) {
  if (n.isRead === 0) {
    await markRead(n.id)
    n.isRead = 1
  }
  if (n.relatedId && n.type === 'comment_reply') {
    router.push(`/forum/${n.relatedId}`)
  }
}

function onLoad() {
  if (loading.value) return
  if (page.value > 1) { page.value++; fetchList() }
}
function onRefresh() { page.value = 1; finished.value = false; fetchList() }
onMounted(() => fetchList())
</script>

<style scoped>
.msg-item { background: #fff; padding: 14px 16px; margin: 0 0 1px; cursor: pointer; }
.msg-item.unread { background: #f0f7ff; }
.msg-title { font-size: 15px; font-weight: 500; display: flex; align-items: center; gap: 6px; }
.dot { width: 8px; height: 8px; border-radius: 50%; background: #ee0a24; flex-shrink: 0; }
.msg-content { font-size: 13px; color: #666; margin: 6px 0; }
.msg-time { font-size: 11px; color: #999; }
</style>
