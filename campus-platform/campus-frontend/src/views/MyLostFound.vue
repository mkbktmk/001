<template>
  <div class="page-container">
    <van-nav-bar title="我的启事" left-arrow @click-left="$router.back()" fixed placeholder />

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" @load="onLoad">
        <div v-for="item in list" :key="item.id" class="lf-card" @click="$router.push(`/lostfound-detail/${item.id}`)">
          <div class="lf-top">
            <van-tag :type="item.type === 'lost' ? 'danger' : 'success'" size="small">
              {{ item.type === 'lost' ? '寻物' : '招领' }}
            </van-tag>
            <van-tag :type="statusColor(item.status)" size="small" style="margin-left:6px">
              {{ statusLabel(item.status) }}
            </van-tag>
            <span class="lf-name">{{ item.itemName }}</span>
          </div>
          <div class="lf-meta">
            <span>📍 {{ item.location }}</span>
            <span>{{ formatTime(item.createTime) }}</span>
          </div>
        </div>
        <van-empty v-if="!loading && list.length === 0" description="还没有发布启事">
          <van-button type="primary" size="small" to="/lostfound-create" style="margin-top:12px">发布启事</van-button>
        </van-empty>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyLostFound } from '../api/lostfound'

const list = ref([])
const page = ref(1)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

function formatTime(t) { return t ? new Date(t).toLocaleDateString('zh-CN') : '' }
function statusLabel(s) { return ({ active: '进行中', found: '已找到' })[s] || s }
function statusColor(s) { return ({ active: 'primary', found: 'success' })[s] || '' }

async function fetchData() {
  loading.value = true
  try {
    const res = await getMyLostFound({ page: page.value, size: 10 })
    const records = res.data?.records || []
    if (page.value === 1) list.value = records
    else list.value.push(...records)
    finished.value = records.length < 10
  } catch {} finally { loading.value = false; refreshing.value = false }
}

function onLoad() { if (loading.value) return; if (page.value > 1) { page.value++; fetchData() } }
function onRefresh() { page.value = 1; finished.value = false; fetchData() }
onMounted(() => fetchData())
</script>

<style scoped>
.lf-card { background: #fff; margin: 8px 16px; padding: 14px; border-radius: 10px; cursor: pointer; }
.lf-top { display: flex; align-items: center; gap: 8px; }
.lf-name { font-size: 16px; font-weight: 500; }
.lf-meta { margin-top: 8px; font-size: 11px; color: #999; display: flex; justify-content: space-between; }
</style>
