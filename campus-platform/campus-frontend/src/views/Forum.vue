<template>
  <div class="forum-page">
    <van-nav-bar title="校园论坛" fixed placeholder>
      <template #right>
        <van-icon name="star-o" size="22" @click="router.push('/my-fav-posts')" style="margin-right:14px" />
        <van-icon name="plus" size="22" @click="router.push('/forum-create')" />
      </template>
    </van-nav-bar>

    <div class="board-bar">
      <span v-for="b in boards" :key="b.value" class="board-chip"
        :class="{ active: activeBoard === b.value }" @click="onBoardSelect(b.value)">
        {{ b.icon }} {{ b.label }}
      </span>
    </div>

    <div class="toolbar">
      <van-search v-model="keyword" shape="round" placeholder="搜索帖子..." @search="onSearch" />
    </div>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" finished-text="— 没有更多了 —" @load="onLoad">
        <PostCard v-for="post in posts" :key="post.id" :post="post" />
        <van-empty v-if="!loading && posts.length === 0" description="暂无帖子，快来发第一帖吧" />
      </van-list>
    </van-pull-refresh>
    <van-back-top />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPostList } from '../api/post'
import PostCard from '../components/PostCard.vue'
const router = useRouter()
const posts = ref([])
const activeBoard = ref('')
const keyword = ref('')
const page = ref(1)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const boards = [
  { label: '全部', value: '', icon: '📋' },{ label: '学习', value: 'study', icon: '📚' },
  { label: '求职', value: 'job', icon: '💼' },{ label: '生活', value: 'life', icon: '🎈' },
  { label: '技术', value: 'tech', icon: '💻' },{ label: '闲聊', value: 'other', icon: '💬' }
]
async function fetchPosts() {
  loading.value = true
  try {
    const res = await getPostList({ page: page.value, size: 10, board: activeBoard.value || undefined, keyword: keyword.value || undefined })
    const records = res.data?.records || []
    if (page.value === 1) posts.value = records; else posts.value.push(...records)
    finished.value = records.length < 10
  } catch {} finally { loading.value = false; refreshing.value = false }
}
function onLoad() { if (loading.value) return; if (page.value > 1) { page.value++; fetchPosts() } }
function onRefresh() { page.value = 1; finished.value = false; fetchPosts() }
function onBoardSelect(v) { activeBoard.value = v; page.value = 1; finished.value = false; fetchPosts() }
function onSearch() { page.value = 1; finished.value = false; fetchPosts() }
onMounted(() => fetchPosts())
</script>

<style scoped>
.forum-page { background: #f5f5f5; min-height: 100vh; }
.board-bar { display: flex; gap: 8px; padding: 10px 16px; background: #fff; overflow-x: auto; white-space: nowrap; -webkit-overflow-scrolling: touch; }
.board-bar::-webkit-scrollbar { display: none; }
.board-chip { display: inline-flex; align-items: center; gap: 4px; padding: 6px 14px; border-radius: 20px; font-size: 13px; background: #f0f0f0; color: #666; cursor: pointer; transition: all .2s; flex-shrink: 0; }
.board-chip.active { background: #1989fa; color: #fff; font-weight: 500; }
.toolbar { background: #fff; padding-bottom: 4px; }
</style>
