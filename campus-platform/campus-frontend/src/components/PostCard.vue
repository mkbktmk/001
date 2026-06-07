<template>
  <div class="post-card" @click="$router.push(`/forum/${post.id}`)">
    <div class="card-top">
      <span class="board-tag">{{ boardMap[post.board] || post.board }}</span>
      <span class="card-title">{{ post.title }}</span>
      <img v-if="coverImage" :src="coverImage" class="card-thumb" alt="" />
    </div>
    <div class="card-meta">
      <span>{{ post.authorName }}</span>
      <span class="meta-dot">·</span>
      <span>{{ formatTime(post.createTime) }}</span>
      <span class="meta-right">
        <van-icon name="eye-o" /> {{ post.viewCount }}
        <van-icon name="like-o" style="margin-left:8px" /> {{ post.likeCount }}
      </span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({ post: Object })

const boardMap = {
  study: '📚 学习', job: '💼 求职', life: '🎈 生活',
  tech: '💻 技术', other: '💬 其他'
}

const coverImage = computed(() => {
  try {
    const imgs = typeof props.post.images === 'string'
      ? JSON.parse(props.post.images)
      : props.post.images
    return Array.isArray(imgs) && imgs.length > 0 ? imgs[0] : null
  } catch { return null }
})

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const diff = now - d
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return d.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.post-card {
  background: #fff;
  padding: 14px 16px;
  margin: 8px 16px;
  border-radius: 10px;
  cursor: pointer;
}
.card-top { display: flex; align-items: flex-start; gap: 8px; }
.board-tag {
  flex-shrink: 0;
  font-size: 12px;
  color: var(--campus-primary);
  background: #e8f4ff;
  padding: 2px 6px;
  border-radius: 4px;
}
.card-title {
  font-size: 15px;
  font-weight: 500;
  line-height: 1.5;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-thumb {
  width: 56px; height: 56px;
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
}
.card-meta {
  margin-top: 10px;
  font-size: 12px;
  color: var(--campus-text-secondary);
  display: flex;
  align-items: center;
}
.meta-dot { margin: 0 6px; }
.meta-right { margin-left: auto; }
</style>
