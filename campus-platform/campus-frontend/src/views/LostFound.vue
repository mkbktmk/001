<template>
  <div class="page-container">
    <van-nav-bar title="失物招领" fixed placeholder>
      <template #right>
        <van-icon name="plus" size="22" @click="$router.push('/lostfound-create')" />
      </template>
    </van-nav-bar>

    <van-tabs v-model:active="activeType" @change="onTabChange">
      <van-tab title="全部" name="" />
      <van-tab title="寻物" name="lost" />
      <van-tab title="招领" name="found" />
    </van-tabs>

    <van-search v-model="keyword" shape="round" placeholder="搜索物品名称或描述" @search="onSearch" />

    <van-dropdown-menu active-color="#1989fa">
      <van-dropdown-item v-model="activeCat" :options="catOptions" @change="onFilterChange" />
    </van-dropdown-menu>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" @load="onLoad">
        <div v-for="item in list" :key="item.id" class="lf-card" @click="$router.push(`/lostfound-detail/${item.id}`)">
          <div class="lf-top">
            <van-tag :type="item.type === 'lost' ? 'danger' : 'success'" size="small">
              {{ item.type === 'lost' ? '寻物' : '招领' }}
            </van-tag>
            <span class="lf-name">{{ item.itemName }}</span>
            <span class="lf-cat">{{ item.category }}</span>
          </div>
          <p class="lf-desc">{{ item.description?.slice(0, 80) }}{{ item.description?.length > 80 ? '...' : '' }}</p>
          <div class="lf-meta">
            <span>📍 {{ item.location }}</span>
            <span>{{ item.userName }} · {{ formatTime(item.createTime) }}</span>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLostFoundList } from '../api/lostfound'

const list = ref([])
const activeType = ref('')
const activeCat = ref('')
const keyword = ref('')
const page = ref(1)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

const catOptions = [
  { text: '全部分类', value: '' },
  { text: '数码', value: 'digital' },
  { text: '证件', value: 'card' },
  { text: '衣物', value: 'clothing' },
  { text: '钥匙', value: 'key' },
  { text: '书籍', value: 'book' },
  { text: '其他', value: 'other' }
]

function formatTime(t) {
  return t ? new Date(t).toLocaleDateString('zh-CN') : ''
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getLostFoundList({
      page: page.value, size: 10,
      type: activeType.value || undefined,
      category: activeCat.value || undefined,
      keyword: keyword.value || undefined
    })
    const records = res.data?.records || []
    if (page.value === 1) list.value = records
    else list.value.push(...records)
    finished.value = records.length < 10
  } catch {} finally { loading.value = false; refreshing.value = false }
}

function onLoad() {
  if (loading.value) return
  if (page.value > 1) { page.value++; fetchData() }
}
function onRefresh() { page.value = 1; finished.value = false; fetchData() }
function onTabChange() { page.value = 1; finished.value = false; fetchData() }
function onFilterChange() { page.value = 1; finished.value = false; fetchData() }
function onSearch() { page.value = 1; finished.value = false; fetchData() }
onMounted(() => fetchData())
</script>

<style scoped>
.lf-card {
  background: #fff;
  margin: 8px 16px;
  padding: 14px;
  border-radius: 10px;
  cursor: pointer;
}
.lf-top { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.lf-name { font-size: 16px; font-weight: 500; }
.lf-cat { font-size: 11px; color: #999; }
.lf-desc { font-size: 13px; color: #666; line-height: 1.5; margin-bottom: 8px; }
.lf-meta { display: flex; justify-content: space-between; font-size: 11px; color: #999; }
</style>
