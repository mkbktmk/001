<template>
  <div class="page-container">
    <van-nav-bar title="我的收藏" left-arrow @click-left="router.back()" fixed placeholder />
    <van-pull-refresh v-model="refreshing" @refresh="fetchData">
      <PostCard v-for="p in posts" :key="p.id" :post="p" />
      <van-empty v-if="posts.length === 0" description="还没有收藏帖子" />
    </van-pull-refresh>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyFavorites } from '../api/post'
import PostCard from '../components/PostCard.vue'
const router = useRouter()
const posts = ref([])
const refreshing = ref(false)
async function fetchData() { try { const r = await getMyFavorites(); posts.value = r.data || [] } catch {} finally { refreshing.value = false } }
onMounted(() => fetchData())
</script>
