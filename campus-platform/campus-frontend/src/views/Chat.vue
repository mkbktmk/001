<template>
  <div class="chat-page">
    <van-nav-bar :title="goodsTitle || '聊天'" left-arrow @click-left="router.back()" fixed placeholder />

    <!-- 商品信息卡片 -->
    <div class="goods-bar">
      <span class="goods-bar-text">📦 {{ goodsTitle }}</span>
      <span class="goods-bar-user">对方：{{ withUserName || '卖家' }}</span>
    </div>

    <div class="chat-body" ref="chatBody">
      <div v-for="(m, i) in messages" :key="m.id">
        <!-- 时间分割线 -->
        <div v-if="showTime(i)" class="time-divider">{{ formatDate(m.createTime) }}</div>

        <div class="msg-row" :class="{ mine: m.senderId === myId }">
          <!-- 头像 -->
          <div class="avatar" :class="{ mine: m.senderId === myId }">
            <van-icon name="user-circle-o" size="36" color="#ccc" />
          </div>
          <div class="msg-content" :class="{ mine: m.senderId === myId }">
            <div class="msg-bubble" :class="{ mine: m.senderId === myId }">
              {{ m.content }}
            </div>
          </div>
        </div>
      </div>
      <van-empty v-if="messages.length === 0" description="暂无消息，打个招呼吧" />
    </div>

    <div class="chat-input">
      <van-field v-model="text" placeholder="输入消息..." :disabled="sending" rows="1" autosize
        @keypress.enter.prevent="handleSend">
        <template #button>
          <van-button size="small" type="primary" round :loading="sending" @click="handleSend">发送</van-button>
        </template>
      </van-field>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMessages, sendMessage } from '../api/message'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const goodsId = ref(Number(route.query.goodsId))
const withUserId = ref(Number(route.query.withUserId))
const goodsTitle = ref(route.query.goodsTitle || '')
const withUserName = ref(route.query.withUserName || '')
const myId = ref(userStore.userInfo?.userId)
const messages = ref([])
const text = ref('')
const sending = ref(false)
const chatBody = ref(null)

function formatDate(t) {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const isToday = d.toDateString() === now.toDateString()
  if (isToday) return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  return d.toLocaleDateString('zh-CN') + ' ' + d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

function showTime(i) {
  if (i === 0) return true
  const prev = new Date(messages.value[i - 1].createTime)
  const curr = new Date(messages.value[i].createTime)
  return (curr - prev) > 5 * 60 * 1000 // 间隔超过5分钟显示时间
}

async function fetchMessages() {
  try {
    const res = await getMessages(goodsId.value, withUserId.value)
    messages.value = res.data || []
    await nextTick()
    scrollBottom()
  } catch {}
}

function scrollBottom() {
  const el = chatBody.value
  if (el) el.scrollTop = el.scrollHeight
}

async function handleSend() {
  if (!text.value.trim()) return
  sending.value = true
  try {
    await sendMessage({
      goodsId: goodsId.value,
      receiverId: withUserId.value,
      content: text.value.trim()
    })
    text.value = ''
    await fetchMessages()
  } catch {} finally { sending.value = false }
}

onMounted(() => fetchMessages())
const timer = setInterval(fetchMessages, 5000)
onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  height: 100dvh; /* dynamic viewport height for mobile browsers */
  background: #f5f5f5;
  overflow: hidden;
}

.chat-page :deep(.van-nav-bar) {
  flex-shrink: 0;
}

.chat-page :deep(.van-nav-bar__placeholder) {
  flex-shrink: 0;
}

/* 确保输入栏始终在底部 */
.chat-input {
  padding: 8px 12px;
  padding-bottom: max(8px, env(safe-area-inset-bottom));
  background: #f7f7f7;
  border-top: 1px solid #ebebeb;
  flex-shrink: 0;
}

.goods-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  background: #fff;
  border-bottom: 1px solid #ebebeb;
  font-size: 13px;
  flex-shrink: 0;
}
.goods-bar-text { color: #333; }
.goods-bar-user { color: #999; }

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 12px 16px;
  -webkit-overflow-scrolling: touch;
}

.time-divider {
  text-align: center;
  margin: 16px 0 10px;
  font-size: 11px;
  color: #b2b2b2;
}

.msg-row {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
}
.msg-row.mine {
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  background: #e5e5e5;
  flex-shrink: 0;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar.mine {
  margin-left: 10px;
}
.avatar:not(.mine) {
  margin-right: 10px;
}

.msg-content {
  max-width: 68%;
}
.msg-content.mine {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.msg-bubble {
  display: inline-block;
  padding: 10px 14px;
  border-radius: 4px 12px 12px 12px;
  background: #fff;
  font-size: 15px;
  line-height: 1.5;
  word-break: break-word;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
.msg-bubble.mine {
  border-radius: 12px 4px 12px 12px;
  background: #95ec69;
  color: #000;
}

.chat-input :deep(.van-cell) {
  background: #fff;
  border-radius: 8px;
  padding: 6px 8px;
}
</style>
