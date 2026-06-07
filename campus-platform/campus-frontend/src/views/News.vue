<template>
  <div class="page-container">
    <van-nav-bar title="校园资讯" fixed placeholder>
      <template #right>
        <van-icon v-if="canPublish" name="plus" size="22" @click="$router.push('/news-create')" />
      </template>
    </van-nav-bar>

    <van-tabs v-model:active="activeCat" @change="onTabChange">
      <van-tab title="全部" name="" />
      <van-tab title="通知" name="notice" />
      <van-tab title="讲座" name="lecture" />
      <van-tab title="活动" name="activity" />
      <van-tab title="就业" name="job" />
    </van-tabs>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" @load="onLoad">
        <div v-for="item in list" :key="item.id" class="news-card"
          @click="$router.push(`/news/${item.id}`)">
          <img v-if="item.coverImage" :src="item.coverImage" class="news-img" />
          <div class="news-body">
            <h3>{{ item.title }}</h3>
            <p class="news-summary">{{ item.summary }}</p>
            <span class="news-meta">
              {{ item.authorName }} · {{ formatTime(item.publishTime) }} · 👁 {{ item.viewCount }}
            </span>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getNewsList } from '../api/news'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const list = ref([])
const activeCat = ref('')
const page = ref(1)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

const canPublish = computed(() => {
  const role = userStore.userInfo?.role
  return role === 'teacher' || role === 'admin'
})

function formatTime(t) {
  if (!t) return ''
  return new Date(t).toLocaleDateString('zh-CN')
}

async function fetchNews() {
  loading.value = true
  try {
    const res = await getNewsList({
      page: page.value,
      size: 10,
      category: activeCat.value || undefined
    })
    const records = res.data?.records || []
    if (page.value === 1) list.value = records
    else list.value.push(...records)
    finished.value = records.length < 10
  } catch {} finally { loading.value = false; refreshing.value = false }
}

function onLoad() {
  if (loading.value) return
  if (page.value > 1) { page.value++; fetchNews() }
}
function onRefresh() { page.value = 1; finished.value = false; fetchNews() }
function onTabChange() { page.value = 1; finished.value = false; fetchNews() }
onMounted(() => fetchNews())
</script>

<style scoped>
.news-card {
  background: #fff;
  margin: 8px 16px;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
}
.news-img {
  width: 100%;
  height: 160px;
  object-fit: cover;
}
.news-body { padding: 12px; }
.news-body h3 { font-size: 16px; margin-bottom: 6px; }
.news-summary { font-size: 13px; color: #666; margin-bottom: 8px; }
.news-meta { font-size: 11px; color: #999; }
</style>
