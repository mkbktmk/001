<template>
  <div class="goods-page">
    <van-nav-bar title="二手交易" fixed placeholder>
      <template #right>
        <van-button size="small" :type="unreadCount > 0 ? 'primary' : 'default'" round @click="router.push('/messages')">
          💬 消息<template v-if="unreadCount > 0"> {{ unreadCount }}</template>
        </van-button>
        <van-icon name="plus" size="22" style="margin-left:8px" @click="router.push('/goods-create')" />
      </template>
    </van-nav-bar>

    <van-search v-model="keyword" shape="round" placeholder="搜索商品" @search="onSearch" />

    <van-tabs v-model:active="activeCat" @change="onCatChange" sticky offset-top="46" swipeable>
      <van-tab v-for="c in categories" :key="c.value" :title="c.label" :name="c.value" />
    </van-tabs>

    <div class="sort-bar">
      <span v-for="o in sortOptions" :key="o.value" class="sort-chip" :class="{ on: sortBy === o.value }" @click="onSortChange(o.value)">{{ o.text }}</span>
      <span class="sort-chip" :class="{ on: hasPriceFilter }" @click="showPrice = !showPrice">💰 价格筛选</span>
    </div>

    <div v-if="showPrice" class="price-row">
      <input v-model="minPrice" type="number" placeholder="最低" class="price-input" />
      <span>—</span>
      <input v-model="maxPrice" type="number" placeholder="最高" class="price-input" />
      <van-button type="primary" size="small" @click="onPriceConfirm">确定</van-button>
      <van-button size="small" @click="onPriceClear">清除</van-button>
    </div>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <div class="goods-grid">
        <GoodsCard v-for="g in list" :key="g.id" :goods="g" />
      </div>
      <van-list v-model:loading="loading" :finished="finished" finished-text="— 到底了 —" @load="onLoad" />
      <van-empty v-if="!loading && list.length === 0" description="暂无商品" />
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getGoodsList } from '../api/goods'
import { getUnreadCount } from '../api/message'
import GoodsCard from '../components/GoodsCard.vue'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const list = ref([])
const unreadCount = ref(0)
const activeCat = ref('')
const keyword = ref('')
const sortBy = ref('latest')
const minPrice = ref('')
const maxPrice = ref('')
const showPrice = ref(false)
const page = ref(1)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

const categories = [
  { label: '全部', value: '' }, { label: '教材', value: 'textbook' },
  { label: '数码', value: 'digital' }, { label: '生活', value: 'living' },
  { label: '服饰', value: 'clothing' }
]
const sortOptions = [
  { text: '最新', value: 'latest' }, { text: '价格↑', value: 'price_asc' }, { text: '价格↓', value: 'price_desc' }
]
const hasPriceFilter = computed(() => minPrice.value || maxPrice.value)

async function fetchGoods() {
  loading.value = true
  try {
    const res = await getGoodsList({ page: page.value, size: 10, category: activeCat.value || undefined, keyword: keyword.value || undefined, sortBy: sortBy.value || undefined, minPrice: minPrice.value ? parseFloat(minPrice.value) : undefined, maxPrice: maxPrice.value ? parseFloat(maxPrice.value) : undefined })
    const records = res.data?.records || []
    if (page.value === 1) list.value = records; else list.value.push(...records)
    finished.value = records.length < 10
  } catch {} finally { loading.value = false; refreshing.value = false }
}

function onLoad() { if (loading.value) return; if (page.value > 1) { page.value++; fetchGoods() } }
function onRefresh() { page.value = 1; finished.value = false; fetchGoods() }
function onCatChange() { page.value = 1; finished.value = false; fetchGoods() }
function onSearch() { page.value = 1; finished.value = false; fetchGoods() }
function onSortChange(v) { sortBy.value = v; page.value = 1; finished.value = false; fetchGoods() }
function onPriceConfirm() { showPrice.value = false; page.value = 1; finished.value = false; fetchGoods() }
function onPriceClear() { minPrice.value = ''; maxPrice.value = ''; showPrice.value = false; page.value = 1; finished.value = false; fetchGoods() }
async function fetchUnread() { if (!userStore.isLoggedIn()) return; try { const r = await getUnreadCount(); unreadCount.value = r.data?.count || 0 } catch {} }

onMounted(() => { fetchGoods(); fetchUnread() })
const timer = setInterval(fetchUnread, 10000)
onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.goods-page { background: #f5f5f5; min-height: 100vh; }
.sort-bar { display: flex; align-items: center; padding: 8px 12px; background: #fff; gap: 6px; border-bottom: 1px solid #f0f0f0; }
.sort-chip { font-size: 12px; color: #666; padding: 5px 10px; border-radius: 14px; cursor: pointer; background: #f5f5f5; }
.sort-chip.on { color: #1989fa; background: #e8f4ff; font-weight: 500; }
.price-row { display: flex; align-items: center; padding: 8px 12px; background: #fff; gap: 6px; border-bottom: 1px solid #f0f0f0; }
.price-input { flex: 1; border: 1px solid #e5e5e5; border-radius: 4px; padding: 6px 8px; font-size: 13px; outline: none; }
.goods-grid { display: flex; flex-direction: column; padding: 8px 0; }
</style>
