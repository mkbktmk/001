<template>
  <div class="detail-page">
    <van-nav-bar title="帖子详情" left-arrow @click-left="router.back()" fixed placeholder />

    <div v-if="post" class="detail-card">
      <h2>{{ post.title }}</h2>
      <div class="detail-meta">
        <span>{{ post.authorName }}</span><span> · </span>
        <span>{{ formatTime(post.createTime) }}</span><span> · </span>
        <span>{{ post.viewCount }} 阅读</span>
        <van-tag v-if="post.board" type="primary" size="small" style="margin-left:8px">{{ boardMap[post.board] }}</van-tag>
      </div>

      <div v-if="postImages.length > 0" class="post-images">
        <img v-for="(url, i) in postImages" :key="i" :src="url" class="post-img" @click="previewImg = url" />
      </div>
      <div v-if="previewImg" class="img-preview" @click="previewImg = null">
        <img :src="previewImg" alt="" />
      </div>

      <div class="detail-content" v-html="post.content"></div>

      <div class="action-bar">
        <div class="action-item" @click="handleLike">
          <van-icon :name="liked ? 'like' : 'like-o'" :color="liked ? '#ee0a24' : ''" size="20" />
          <span>{{ post.likeCount }}</span>
        </div>
        <div class="action-item" @click="handleFav">
          <van-icon :name="favorited ? 'star' : 'star-o'" :color="favorited ? '#ff976a' : ''" size="20" />
          <span>收藏</span>
        </div>
        <div class="action-item" @click="focusInput">
          <van-icon name="comment-o" size="20" />
          <span>{{ post.commentCount }}</span>
        </div>
        <div v-if="isOwner" class="action-item" @click="handleDelete">
          <van-icon name="delete-o" color="#ee0a24" size="20" />
          <span style="color:#ee0a24">删除</span>
        </div>
      </div>
    </div>

    <div class="comment-section">
      <div class="section-title">全部回复 ({{ comments.length }})</div>
      <div v-for="(c, idx) in comments" :key="c.id" class="comment-item"
        @dblclick="isAdmin && showCommentActions(c)">
        <div class="comment-header">
          <span class="comment-floor">#{{ idx + 1 }}</span>
          <span class="comment-author">{{ c.authorName }}</span>
          <span class="comment-time">{{ formatTime(c.createTime) }}</span>
          <van-icon v-if="isAdmin" name="ellipsis" size="16" color="#ccc" @click.stop="showCommentActions(c)" style="cursor:pointer" />
        </div>
        <div class="comment-content">{{ c.content }}</div>
      </div>
      <van-empty v-if="!loading && comments.length === 0" description="暂无回复，快来抢沙发" />
      <div style="height:60px"></div>
    </div>

    <div class="reply-bar">
      <input ref="replyInput" v-model="commentText" class="reply-input" placeholder="写下你的回复..." @keyup.enter="handleComment" />
      <van-button size="small" type="primary" round :loading="sending" @click="handleComment">发送</van-button>
    </div>

    <!-- 管理操作面板 -->
    <van-dialog v-model:show="showMutePicker" title="设置禁言时长" show-cancel-button @confirm="confirmMute" class="mute-dialog">
      <div class="mute-body">
        <div class="mute-presets">
          <span v-for="p in mutePresets" :key="p.label" class="mute-preset"
            :class="{ active: muteUnit === p.unit && muteValue === p.value }"
            @click="muteUnit = p.unit; muteValue = p.value">{{ p.label }}</span>
        </div>
        <div class="mute-custom">
          <span class="mute-label">自定义：</span>
          <input v-model.number="muteValue" type="number" min="1" max="999" class="mute-input" @focus="muteUnit = muteUnit || 'hour'" />
          <select v-model="muteUnit" class="mute-select">
            <option value="minute">分钟</option>
            <option value="hour">小时</option>
            <option value="day">天</option>
            <option value="year">年</option>
          </select>
        </div>
        <div class="mute-summary">禁言 <strong>{{ targetComment?.authorName }}</strong> {{ formatMute() }}</div>
      </div>
    </van-dialog>

    <van-action-sheet v-model:show="showSheet" :actions="sheetActions" @select="onSheetSelect" cancel-text="取消" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPostDetail, toggleLike, toggleFavorite, isLiked, isFavorited, getComments, createComment, deletePost } from '../api/post'
import { deleteComment } from '../api/post'
import { useUserStore } from '../stores/user'
import { showConfirmDialog, showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const post = ref(null)
const comments = ref([])
const liked = ref(false)
const favorited = ref(false)
const commentText = ref('')
const loading = ref(false)
const sending = ref(false)
const previewImg = ref(null)
const replyInput = ref(null)
const showSheet = ref(false)
const targetComment = ref(null)
const showMutePicker = ref(false)
const muteValue = ref(1)
const muteUnit = ref('hour')
const isTargetMuted = ref(false)

const mutePresets = [
  { label: '10分钟', value: 10, unit: 'minute' },
  { label: '1小时', value: 1, unit: 'hour' },
  { label: '6小时', value: 6, unit: 'hour' },
  { label: '1天', value: 1, unit: 'day' },
  { label: '3天', value: 3, unit: 'day' },
  { label: '7天', value: 7, unit: 'day' },
  { label: '1年', value: 1, unit: 'year' },
  { label: '10年', value: 10, unit: 'year' }
]

const isAdmin = computed(() => userStore.userInfo?.role === 'admin')

const isOwner = computed(() => {
  return userStore.userInfo?.userId && post.value?.authorId &&
    (userStore.userInfo.userId === post.value.authorId || isAdmin.value)
})

const sheetActions = computed(() => {
  const acts = [{ name: '删除回复', color: '#ee0a24' }]
  if (targetComment.value) {
    if (isTargetMuted.value) {
      acts.push({ name: '解除禁言 ' + targetComment.value.authorName, color: '#07c160' })
    } else {
      acts.push({ name: '禁言 ' + targetComment.value.authorName, color: '#ff976a' })
    }
  }
  return acts
})

const postImages = computed(() => {
  try { const imgs = post.value?.images; if (!imgs) return []; const p = typeof imgs === 'string' ? JSON.parse(imgs) : imgs; return Array.isArray(p) ? p : [] } catch { return [] }
})

const boardMap = { study: '学习', job: '求职', life: '生活', tech: '技术', other: '闲聊' }

function formatTime(t) {
  if (!t) return ''; const d = new Date(t); return d.toLocaleDateString('zh-CN') + ' ' + d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

function focusInput() { replyInput.value?.focus() }

async function showCommentActions(c) {
  targetComment.value = c
  try {
    const res = await fetch('/api/comment/mute/' + c.authorId + '/status', {
      headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
    })
    if (res.ok) { const d = await res.json(); isTargetMuted.value = d.data === true }
  } catch { isTargetMuted.value = false }
  showSheet.value = true
}

function formatMute() {
  const v = muteValue.value, u = muteUnit.value
  if (u === 'minute') return v + ' 分钟'
  if (u === 'hour') return v + ' 小时'
  if (u === 'day') return v + ' 天'
  return v + ' 年'
}
function calcMinutes() {
  const v = muteValue.value
  switch (muteUnit.value) {
    case 'minute': return v
    case 'hour': return v * 60
    case 'day': return v * 1440
    case 'year': return v * 525600
  }
  return 60
}
async function confirmMute() {
  const mins = calcMinutes()
  if (mins > 5256000) { showToast('最长10年'); return }
  try {
    await fetch('/api/comment/mute?userId=' + targetComment.value.authorId + '&minutes=' + mins, {
      method: 'POST', headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
    })
    showToast('已禁言 ' + formatMute())
    showMutePicker.value = false
  } catch { showToast('操作失败') }
}

async function onSheetSelect(item) {
  showSheet.value = false
  if (!targetComment.value) return
  if (item.name.includes('删除')) {
    await showConfirmDialog({ title: '确认删除该回复？' })
    try { await deleteComment(targetComment.value.id); showToast('已删除'); fetchComments() } catch {}
  } else if (item.name.includes('解除禁言')) {
    await showConfirmDialog({ title: '确认解除禁言？' })
    try {
      await fetch('/api/comment/mute/' + targetComment.value.authorId, {
        method: 'DELETE', headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
      })
      showToast('已解除禁言')
    } catch {}
  } else if (item.name.includes('禁言')) {
    muteValue.value = 1; muteUnit.value = 'hour'; showMutePicker.value = true
  }
}

async function handleLike() {
  if (!userStore.isLoggedIn()) return router.push('/login')
  try { const res = await toggleLike(post.value.id); liked.value = !liked.value; post.value.likeCount = res.data } catch {}
}
async function handleFav() {
  if (!userStore.isLoggedIn()) return router.push('/login')
  try { const res = await toggleFavorite(post.value.id); favorited.value = res.data } catch {}
}
async function handleComment() {
  if (!userStore.isLoggedIn()) return router.push('/login')
  if (!commentText.value.trim()) return
  sending.value = true
  try { await createComment({ postId: post.value.id, content: commentText.value }); commentText.value = ''; fetchComments() } catch (e) { sending.value = false; showConfirmDialog({ title: '提示', message: e?.message || '回复失败', showCancelButton: false }) }
  finally { sending.value = false }
}
async function handleDelete() {
  await showConfirmDialog({ title: '确认删除', message: '删除后不可恢复' })
  try { await deletePost(post.value.id); showToast('已删除'); setTimeout(() => router.back(), 800) } catch {}
}

async function fetchComments() {
  loading.value = true
  try { const res = await getComments(post.value.id); comments.value = res.data || []; if (post.value) post.value.commentCount = comments.value.length } catch {}
  finally { loading.value = false }
}

onMounted(async () => {
  try {
    const res = await getPostDetail(route.params.id); post.value = res.data
    if (userStore.isLoggedIn()) { const [l, f] = await Promise.all([isLiked(route.params.id), isFavorited(route.params.id)]); liked.value = l.data; favorited.value = f.data }
    fetchComments()
  } catch {}
})
</script>

<style scoped>
.detail-page { background: #f5f5f5; min-height: 100vh; }
.detail-card { background: #fff; padding: 16px; margin-bottom: 8px; }
.detail-card h2 { font-size: 20px; line-height: 1.4; margin-bottom: 10px; }
.detail-meta { font-size: 12px; color: #999; margin-bottom: 16px; display: flex; align-items: center; gap: 4px; flex-wrap: wrap; }
.detail-content { font-size: 15px; line-height: 1.8; white-space: pre-wrap; word-break: break-word; }
.detail-content :deep(img) { max-width: 100%; }
.post-images { display: flex; flex-wrap: wrap; gap: 8px; margin: 12px 0; }
.post-img { width: 100px; height: 100px; object-fit: cover; border-radius: 6px; cursor: pointer; }
.img-preview { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,.9); z-index: 9999; display: flex; align-items: center; justify-content: center; }
.img-preview img { max-width: 100%; max-height: 100%; object-fit: contain; }

.action-bar { display: flex; justify-content: space-around; padding: 14px 0; margin-top: 16px; border-top: 1px solid #f0f0f0; }
.action-item { display: flex; flex-direction: column; align-items: center; gap: 4px; font-size: 12px; color: #999; cursor: pointer; }

.comment-section { background: #fff; padding: 16px; }
.section-title { font-size: 15px; font-weight: 600; margin-bottom: 12px; }
.comment-item { padding: 12px 0; border-bottom: 1px solid #f5f5f5; }
.comment-header { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.comment-floor { font-size: 11px; color: #1989fa; font-weight: 600; }
.comment-author { font-size: 13px; font-weight: 500; }
.comment-time { font-size: 11px; color: #999; margin-left: auto; }
.comment-content { font-size: 14px; color: #555; line-height: 1.6; }

.reply-bar { position: fixed; bottom: 0; left: 0; right: 0; background: #fff; display: flex; align-items: center; padding: 8px 12px; gap: 8px; box-shadow: 0 -2px 8px rgba(0,0,0,.06); z-index: 100; padding-bottom: env(safe-area-inset-bottom); }
.reply-input { flex: 1; border: none; outline: none; background: #f5f5f5; border-radius: 20px; padding: 10px 16px; font-size: 14px; }

.mute-body { padding: 4px 16px 16px; }
.mute-presets { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 14px; }
.mute-preset { font-size: 13px; padding: 6px 12px; border-radius: 16px; background: #f5f5f5; color: #666; cursor: pointer; transition: all .2s; }
.mute-preset.active { background: #1989fa; color: #fff; }
.mute-custom { display: flex; align-items: center; gap: 6px; margin-bottom: 12px; }
.mute-label { font-size: 13px; color: #999; flex-shrink: 0; }
.mute-input { width: 60px; border: 1px solid #e5e5e5; border-radius: 6px; padding: 6px 8px; font-size: 14px; text-align: center; outline: none; }
.mute-select { border: 1px solid #e5e5e5; border-radius: 6px; padding: 6px; font-size: 14px; outline: none; background: #fff; }
.mute-summary { text-align: center; font-size: 14px; color: #333; padding: 10px; background: #f7f8fa; border-radius: 8px; }
.mute-summary strong { color: #1989fa; }
</style>
