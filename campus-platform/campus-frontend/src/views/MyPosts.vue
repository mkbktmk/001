<template>
  <div class="page-container">
    <van-nav-bar title="我的帖子" left-arrow @click-left="$router.back()" fixed placeholder />

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" finished-text="— 没有更多了 —" @load="onLoad">
        <PostCard v-for="post in posts" :key="post.id" :post="post" />
        <van-empty v-if="!loading && posts.length === 0" description="你还没有发帖，快去发布吧">
          <van-button type="primary" size="small" to="/forum-create" style="margin-top:12px">去发帖</van-button>
        </van-empty>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyPosts } from '../api/post'
import PostCard from '../components/PostCard.vue'

const posts = ref([])
const page = ref(1)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

async function fetchPosts() {
  loading.value = true
  try {
    const res = await getMyPosts({ page: page.value, size: 10 })
    const records = res.data?.records || []
    if (page.value === 1) posts.value = records
    else posts.value.push(...records)
    finished.value = records.length < 10
  } catch {} finally { loading.value = false; refreshing.value = false }
}

function onLoad() {
  if (loading.value) return
  if (page.value > 1) { page.value++; fetchPosts() }
}
function onRefresh() { page.value = 1; finished.value = false; fetchPosts() }
onMounted(() => fetchPosts())
</script>
