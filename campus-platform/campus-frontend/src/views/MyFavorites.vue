<template>
  <div class="page-container">
    <van-nav-bar title="我的收藏" left-arrow @click-left="$router.back()" fixed placeholder />

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" @load="fetchFavorites">
        <GoodsCard v-for="g in list" :key="g.id" :goods="g" />
        <van-empty v-if="!loading && list.length === 0" description="还没有收藏任何商品" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyFavorites } from '../api/goods'
import GoodsCard from '../components/GoodsCard.vue'

const list = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

async function fetchFavorites() {
  loading.value = true
  try {
    const res = await getMyFavorites()
    list.value = res.data || []
    finished.value = true
  } catch {} finally {
    loading.value = false
    refreshing.value = false
  }
}

function onRefresh() { finished.value = false; fetchFavorites() }
onMounted(() => fetchFavorites())
</script>
