<template>
  <div class="page-container">
    <van-nav-bar title="我的发布" left-arrow @click-left="$router.back()" fixed placeholder />

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" @load="onLoad">
        <div v-for="g in list" :key="g.id" class="goods-card" @click="$router.push(`/goods/${g.id}`)">
          <div class="goods-img">
            <img v-if="coverImage(g)" :src="coverImage(g)" class="cover-img" alt="" />
            <van-icon v-else name="photo-o" size="40" color="#ddd" />
          </div>
          <div class="goods-info">
            <div class="goods-top">
              <h4>{{ g.title }}</h4>
              <van-tag :type="statusTag(g.status)" size="small">{{ statusLabel(g.status) }}</van-tag>
            </div>
            <div class="goods-bottom">
              <span class="goods-price">¥{{ g.price }}</span>
              <span style="margin-left:auto;font-size:12px;color:#999">⭐ {{ g.favCount }} · 👁 {{ g.viewCount }}</span>
            </div>
          </div>
        </div>
        <van-empty v-if="!loading && list.length === 0" description="还没有发布任何商品" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyGoodsList } from '../api/goods'

const list = ref([])
const page = ref(1)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

function coverImage(g) {
  try {
    const imgs = typeof g.images === 'string' ? JSON.parse(g.images) : g.images
    return Array.isArray(imgs) && imgs.length > 0 ? imgs[0] : null
  } catch { return null }
}

function statusLabel(s) {
  return { active: '在售', sold: '已售', offline: '已下架' }[s] || s
}

function statusTag(s) {
  return { active: 'primary', sold: 'danger', offline: 'default' }[s] || 'default'
}

async function fetchGoods() {
  loading.value = true
  try {
    const res = await getMyGoodsList({ page: page.value, size: 10 })
    const records = res.data?.records || []
    if (page.value === 1) list.value = records
    else list.value.push(...records)
    finished.value = records.length < 10
  } catch {} finally { loading.value = false; refreshing.value = false }
}

function onLoad() {
  if (loading.value) return
  if (page.value > 1) { page.value++; fetchGoods() }
}
function onRefresh() { page.value = 1; finished.value = false; fetchGoods() }
onMounted(() => fetchGoods())
</script>

<style scoped>
.goods-card {
  display: flex;
  background: #fff;
  margin: 8px 16px;
  padding: 12px;
  border-radius: 10px;
  cursor: pointer;
}
.goods-img {
  width: 80px; height: 80px;
  border-radius: 8px;
  background: #f5f5f5;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
}
.cover-img { width: 100%; height: 100%; object-fit: cover; }
.goods-info {
  flex: 1; padding-left: 12px;
  display: flex; flex-direction: column;
  overflow: hidden;
}
.goods-top { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.goods-top h4 { font-size: 15px; flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.goods-bottom { display: flex; align-items: baseline; margin-top: auto; }
.goods-price { font-size: 18px; font-weight: 700; color: #ee0a24; }
</style>
