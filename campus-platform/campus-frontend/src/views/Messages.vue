<template>
  <div class="messages-page">
    <van-nav-bar title="消息" left-arrow @click-left="router.back()" fixed placeholder />

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <div v-for="c in conversations" :key="c.goodsId" class="conv-item" @click="openChat(c)">
        <div class="conv-avatar">
          <van-icon name="user-circle-o" size="48" color="#ccc" />
        </div>
        <div class="conv-body">
          <div class="conv-top">
            <span class="conv-name">{{ c.withUserName || c.goodsTitle || '聊天' }}</span>
            <span class="conv-time">{{ formatTime(c.lastTime) }}</span>
          </div>
          <div class="conv-bottom">
            <span class="conv-msg">
              <span class="conv-goods">[{{ c.goodsTitle }}]</span> {{ c.lastMessage }}
            </span>
            <van-badge v-if="c.unread > 0" :content="c.unread > 99 ? '99+' : c.unread" />
          </div>
        </div>
      </div>
    </van-pull-refresh>

    <van-empty v-if="!loading && conversations.length === 0" image="search" description="暂无消息">
      <p style="color:#999;font-size:13px">去二手市场逛逛，跟卖家聊聊吧</p>
    </van-empty>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getConversations } from '../api/message'

const router = useRouter()
const conversations = ref([])
const loading = ref(false)
const refreshing = ref(false)

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const yesterday = new Date(today - 86400000)
  if (d >= today) return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  if (d >= yesterday) return '昨天'
  if (d.getFullYear() === now.getFullYear()) return d.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
  return d.toLocaleDateString('zh-CN')
}

function openChat(c) {
  router.push({
    path: '/chat',
    query: {
      goodsId: c.goodsId,
      withUserId: c.withUser,
      goodsTitle: c.goodsTitle,
      withUserName: c.withUserName
    }
  })
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getConversations()
    conversations.value = res.data || []
  } catch {} finally { loading.value = false; refreshing.value = false }
}

function onRefresh() { fetchData() }
onMounted(() => fetchData())
const timer = setInterval(fetchData, 10000)
onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.messages-page {
  min-height: 100vh;
  background: #f5f5f5;
}
.conv-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  background: #fff;
  cursor: pointer;
  user-select: none;
  -webkit-tap-highlight-color: transparent;
}
.conv-item:active { background: #f0f0f0; }
.conv-item:not(:last-child) { border-bottom: 1px solid #f0f0f0; }
.conv-avatar {
  margin-right: 12px;
  flex-shrink: 0;
  width: 48px; height: 48px;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.conv-body { flex: 1; min-width: 0; }
.conv-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px; }
.conv-name { font-size: 16px; font-weight: 500; color: #333; }
.conv-time { font-size: 12px; color: #b0b0b0; flex-shrink: 0; margin-left: 8px; }
.conv-bottom { display: flex; justify-content: space-between; align-items: center; }
.conv-msg { font-size: 13px; color: #999; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.conv-goods { color: #1989fa; font-size: 12px; }
</style>
