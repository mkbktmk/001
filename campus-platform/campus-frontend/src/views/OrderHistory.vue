<template>
  <div class="page-container">
    <van-nav-bar title="购买记录" left-arrow @click-left="router.back()" fixed placeholder />

    <div v-for="o in orders" :key="o.id" class="order-card" @click="router.push(`/goods/${o.goodsId}`)">
      <div class="order-header">
        <span class="order-id">订单 #{{ o.id }}</span>
        <van-tag type="success" size="small">{{ o.status === 'paid' ? '已支付' : o.status }}</van-tag>
      </div>
      <div class="order-body">
        <div class="order-goods">
          <span class="order-title">{{ o.goodsTitle }}</span>
          <span class="order-amount">¥{{ o.amount }}</span>
        </div>
        <div class="order-info">
          <span>卖家：{{ o.sellerName }}</span>
          <span class="order-time">{{ formatTime(o.createTime) }}</span>
        </div>
      </div>
      <div class="order-arrow">
        <van-icon name="arrow" color="#ccc" />
      </div>
    </div>

    <van-empty v-if="orders.length === 0" description="暂无购买记录">
      <van-button type="primary" size="small" to="/goods" style="margin-top:12px">去逛逛</van-button>
    </van-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyOrders } from '../api/order'

const router = useRouter()
const orders = ref([])

function formatTime(t) { return t ? new Date(t).toLocaleDateString('zh-CN') + ' ' + new Date(t).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) : '' }

onMounted(async () => {
  try { const res = await getMyOrders(); orders.value = res.data || [] } catch {}
})
</script>

<style scoped>
.order-card {
  background: #fff;
  margin: 8px 16px;
  padding: 14px 16px;
  border-radius: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
}
.order-card:active { background: #f9f9f9; }
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.order-id { font-size: 12px; color: #999; }
.order-body { flex: 1; min-width: 0; }
.order-goods { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.order-title { font-size: 15px; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 65%; }
.order-amount { font-size: 18px; font-weight: 700; color: #ee0a24; flex-shrink: 0; }
.order-info { display: flex; justify-content: space-between; font-size: 12px; color: #999; }
.order-time { flex-shrink: 0; }
.order-arrow { margin-left: 8px; flex-shrink: 0; }
</style>
