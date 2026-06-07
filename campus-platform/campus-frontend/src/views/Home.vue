<template>
  <div class="home-page">
    <!-- 顶部搜索 -->
    <div class="home-header">
      <div class="header-title">
        <h1>🏫 校园服务</h1>
        <p>欢迎回来</p>
      </div>
      <van-search v-model="keyword" shape="round" placeholder="搜索帖子" @search="goSearch" background="transparent" />
    </div>

    <!-- 轮播公告 -->
    <div class="notice-wrap">
      <van-swipe ref="noticeSwipe" :autoplay="3000" class="notice-swipe" indicator-color="#fff" :show-indicators="notices.length > 1">
        <van-swipe-item v-for="item in notices" :key="item.id" @click="router.push(`/news/${item.id}`)">
          <div class="notice-card">
            <img v-if="item.coverImage" :src="item.coverImage" class="notice-bg" alt="" />
            <div class="notice-overlay">
              <span class="notice-tag">📢 校园资讯</span>
              <span class="notice-title">{{ item.title }}</span>
            </div>
          </div>
        </van-swipe-item>
      </van-swipe>
      <span v-if="notices.length > 1" class="swipe-arrow left" @click.stop="noticeSwipe?.prev()">‹</span>
      <span v-if="notices.length > 1" class="swipe-arrow right" @click.stop="noticeSwipe?.next()">›</span>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-actions">
      <div class="action-item" v-for="a in actions" :key="a.path" @click="router.push(a.path)">
        <span class="action-icon">{{ a.icon }}</span>
        <span class="action-text">{{ a.text }}</span>
      </div>
    </div>

    <!-- 最新帖子 -->
    <div class="section">
      <div class="section-head">
        <span class="section-title">📝 最新帖子</span>
        <router-link to="/forum" class="section-more">全部 →</router-link>
      </div>
      <PostCard v-for="post in posts" :key="post.id" :post="post" />
    </div>

    <!-- 热门帖子 -->
    <div class="section" v-if="hotPosts.length > 0">
      <div class="section-head">
        <span class="section-title">🔥 热门帖子</span>
      </div>
      <PostCard v-for="post in hotPosts" :key="'hot-'+post.id" :post="post" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNewsList } from '../api/news'
import { getPostList } from '../api/post'
import PostCard from '../components/PostCard.vue'

const router = useRouter()
const noticeSwipe = ref(null)
const keyword = ref('')
const notices = ref([])
const posts = ref([])
const hotPosts = ref([])

const actions = [
  { icon: '📰', text: '校园资讯', path: '/news' },
  { icon: '🔍', text: '失物招领', path: '/lostfound' },
  { icon: '🛒', text: '二手交易', path: '/goods' },
  { icon: '🔧', text: '报修投诉', path: '/complaint' }
]

function goSearch() { router.push({ path: '/forum', query: { keyword: keyword.value } }) }

onMounted(async () => {
  try { const r = await getNewsList({ page: 1, size: 3 }); notices.value = r.data?.records || [] } catch {}
  try {
    const r = await getPostList({ page: 1, size: 5 })
    posts.value = r.data?.records || []
    hotPosts.value = [...posts.value].sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0)).slice(0, 3)
  } catch {}
})
</script>

<style scoped>
.home-page { background: #f5f5f5; min-height: 100vh; padding-bottom: 50px; }
.home-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 16px 16px 8px;
}
.home-header h1 { font-size: 22px; color: #fff; margin: 0 0 4px; }
.home-header p { font-size: 13px; color: rgba(255,255,255,0.7); margin: 0 0 8px; }

.notice-wrap { position: relative; margin: 12px 16px; }
.notice-swipe { border-radius: 10px; overflow: hidden; height: 120px; }
.swipe-arrow {
  position: absolute; top: 50%; transform: translateY(-50%);
  width: 28px; height: 28px; border-radius: 50%;
  background: rgba(255,255,255,0.8); color: #666;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px; font-weight: bold; cursor: pointer;
  z-index: 2; user-select: none;
}
.swipe-arrow.left { left: 6px; }
.swipe-arrow.right { right: 6px; }
.swipe-arrow:active { background: #fff; }
.notice-card { position: relative; width: 100%; height: 120px; border-radius: 10px; overflow: hidden; }
.notice-bg { width: 100%; height: 100%; object-fit: cover; position: absolute; top: 0; left: 0; }
.notice-overlay {
  position: absolute; bottom: 0; left: 0; right: 0;
  padding: 24px 16px 12px;
  background: linear-gradient(transparent, rgba(0,0,0,0.6));
  color: #fff;
  display: flex; flex-direction: column; gap: 4px;
}
.notice-tag { font-size: 11px; }
.notice-title { font-size: 14px; font-weight: 500; }

.quick-actions {
  display: flex;
  justify-content: space-around;
  padding: 16px;
  background: #fff;
  margin: 12px 16px;
  border-radius: 12px;
}
.action-item { display: flex; flex-direction: column; align-items: center; gap: 6px; cursor: pointer; }
.action-icon { font-size: 28px; }
.action-text { font-size: 12px; color: #666; }

.section { margin-top: 8px; }
.section-head { display: flex; justify-content: space-between; align-items: center; padding: 16px 16px 8px; }
.section-title { font-size: 16px; font-weight: 600; }
.section-more { font-size: 13px; color: #1989fa; text-decoration: none; }
</style>
