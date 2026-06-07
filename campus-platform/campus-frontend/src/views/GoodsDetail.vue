<template>
  <div class="detail-page">
    <van-nav-bar title="商品详情" left-arrow @click-left="router.back()" />

    <!-- 图片轮播 -->
    <div v-if="imageList.length > 0" class="img-swipe">
      <van-swipe :autoplay="3000" indicator-color="#fff" lazy-render>
        <van-swipe-item v-for="(url, idx) in imageList" :key="idx">
          <img :src="url" class="detail-img" alt="" />
        </van-swipe-item>
      </van-swipe>
    </div>
    <div v-else class="img-placeholder">
      <van-icon name="photo-o" size="48" color="#ccc" />
    </div>

    <!-- 价格卡片 -->
    <div class="price-card">
      <div class="price-main">¥{{ goods?.price }}</div>
      <div class="price-sub">
        <span v-if="goods?.originalPrice" class="original">¥{{ goods?.originalPrice }}</span>
        <van-tag type="danger" size="medium" plain>{{ conditionMap[goods?.goodsCondition] || goods?.goodsCondition }}</van-tag>
      </div>
      <div class="status-tag">
        <van-tag v-if="goods?.status === 'sold'" type="danger" size="large">已售</van-tag>
        <van-tag v-if="goods?.status === 'offline'" type="default" size="large">已下架</van-tag>
      </div>
    </div>

    <!-- 标题 -->
    <div class="section title-section">
      <h1>{{ goods?.title }}</h1>
      <div class="meta-row">
        <span>👁 {{ goods?.viewCount || 0 }} 浏览</span>
        <span>·</span>
        <span>⭐ {{ goods?.favCount || 0 }} 收藏</span>
        <span>·</span>
        <span>{{ goods?.createTime ? new Date(goods.createTime).toLocaleDateString('zh-CN') : '' }}</span>
      </div>
    </div>

    <!-- 卖家信息 -->
    <div class="section seller-card">
      <div class="seller-top">
        <div class="seller-avatar">
          <van-icon name="user-circle-o" size="40" color="#999" />
        </div>
        <div class="seller-info">
          <div class="seller-name">{{ goods?.sellerName }}</div>
          <div class="seller-contact">📞 {{ maskedContact }}</div>
        </div>
      </div>
    </div>

    <!-- 商品描述 -->
    <div class="section desc-section" v-if="goods?.description">
      <div class="section-title">商品描述</div>
      <div class="desc-content">{{ goods.description }}</div>
    </div>

    <!-- 底部占位 -->
    <div style="height:56px"></div>

    <!-- 固定底栏 -->
    <div class="bottom-bar">
      <!-- 买家：在售(已登录) -->
      <template v-if="!isSeller && goods?.status === 'active' && userStore.isLoggedIn()">
        <div class="bar-icons">
          <span class="bar-icon" @click="handleFav">
            <van-icon :name="favorited ? 'star' : 'star-o'" size="20" :color="favorited ? '#ff976a' : '#666'" />
            <label>{{ favorited ? '已收藏' : '收藏' }}</label>
          </span>
          <span class="bar-icon" @click="handleChat">
            <van-icon name="chat-o" size="20" color="#666" />
            <label>私聊</label>
          </span>
        </div>
        <van-button type="danger" class="bar-btn-main" style="margin-left:auto" @click="showPay = true">立即购买</van-button>
      </template>

      <!-- 买家：在售(未登录) -->
      <template v-if="!isSeller && goods?.status === 'active' && !userStore.isLoggedIn()">
        <span class="bar-icon" @click="handleFav">
          <van-icon name="star-o" size="20" color="#666" />
          <label>收藏</label>
        </span>
        <van-button type="danger" class="bar-btn-main" style="margin-left:auto" @click="router.push('/login')">登录后购买</van-button>
      </template>

      <!-- 卖家：在售 -->
      <template v-if="isSeller && goods?.status === 'active'">
        <span class="bar-icon" @click="handleFav">
          <van-icon :name="favorited ? 'star' : 'star-o'" size="20" color="#666" />
          <label>收藏</label>
        </span>
        <van-button type="warning" class="bar-btn-half" @click="handleMarkSold">标记已售</van-button>
        <van-button type="default" class="bar-btn-half" @click="handleOffline">下架</van-button>
      </template>

      <!-- 卖家：已下架 -->
      <template v-if="isSeller && goods?.status === 'offline'">
        <span class="bar-icon" @click="handleFav">
          <van-icon name="star-o" size="20" color="#666" />
          <label>收藏</label>
        </span>
        <van-button type="primary" class="bar-btn-half" @click="handleEdit">编辑</van-button>
        <van-button type="success" class="bar-btn-half" @click="handleRelist">重新上架</van-button>
      </template>

      <!-- 已售/已下架 -->
      <template v-if="goods?.status === 'sold' || (!isSeller && goods?.status === 'offline')">
        <div class="bar-status">{{ goods?.status === 'sold' ? '该商品已售出' : '该商品已下架' }}</div>
      </template>
    </div>

    <!-- 购买弹窗 -->
    <van-dialog v-model:show="showPay" title="确认购买" show-cancel-button @confirm="handlePay">
      <div class="pay-body">
        <div class="pay-item">
          <span class="pay-label">商品</span>
          <span>{{ goods?.title }}</span>
        </div>
        <div class="pay-item">
          <span class="pay-label">金额</span>
          <span class="pay-price">¥{{ goods?.price }}</span>
        </div>
        <div class="pay-methods">
          <span class="pay-label">支付方式</span>
          <van-radio-group v-model="payMethod" direction="horizontal">
            <van-radio name="wechat">💚 微信</van-radio>
            <van-radio name="alipay">💙 支付宝</van-radio>
          </van-radio-group>
        </div>
        <div class="qr-placeholder">
          <van-icon name="qr" size="64" color="#1989fa" />
          <p>{{ payMethod === 'wechat' ? '微信扫码支付' : '支付宝扫码支付' }}</p>
          <p class="qr-tip">校园 Demo · 点击确认模拟支付</p>
        </div>
      </div>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getGoodsDetail, toggleGoodsFavorite, isGoodsFavorited, changeGoodsStatus } from '../api/goods'
import { createOrder } from '../api/order'
import { useUserStore } from '../stores/user'
import { showConfirmDialog, showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const goods = ref(null)
const favorited = ref(false)
const showPay = ref(false)
const payMethod = ref('wechat')

const isSeller = computed(() => {
  return userStore.userInfo?.userId && goods.value?.sellerId &&
    userStore.userInfo.userId === goods.value.sellerId
})

const maskedContact = computed(() => {
  const c = goods.value?.contact || ''
  if (!c) return '未填写'
  const phone = /^(1[3-9]\d)\d{4}(\d{4})$/
  const m = c.match(phone)
  if (m) return m[1] + '****' + m[2]
  const email = /^(.{1,3}).*(@.*)$/
  const em = c.match(email)
  if (em) return em[1] + '***' + em[2]
  return c
})

const imageList = computed(() => {
  try {
    const imgs = goods.value?.images
    if (!imgs) return []
    const parsed = typeof imgs === 'string' ? JSON.parse(imgs) : imgs
    return Array.isArray(parsed) ? parsed : []
  } catch { return [] }
})

const conditionMap = { new: '全新', like_new: '几乎全新', good: '良好', fair: '一般' }

async function handleFav() {
  if (!userStore.isLoggedIn()) return router.push('/login')
  try {
    const res = await toggleGoodsFavorite(goods.value.id)
    favorited.value = res.data
    goods.value.favCount += favorited.value ? 1 : -1
  } catch {}
}
async function handleOffline() {
  await showConfirmDialog({ title: '确认下架', message: '下架后将不再公开展示' })
  try { await changeGoodsStatus(goods.value.id, 'offline'); goods.value.status = 'offline'; showToast('已下架') } catch {}
}
function handleEdit() { router.push(`/goods-edit/${goods.value.id}`) }
async function handleMarkSold() {
  await showConfirmDialog({ title: '确认已售', message: '标记已售后商品将不再展示' })
  try { await changeGoodsStatus(goods.value.id, 'sold'); goods.value.status = 'sold'; showToast('已售') } catch {}
}
async function handlePay() {
  try {
    await createOrder({ goodsId: goods.value.id })
    goods.value.status = 'sold'
    showToast('购买成功！')
  } catch {}
}
function handleChat() {
  router.push({ path: '/chat', query: { goodsId: goods.value.id, withUserId: goods.value.sellerId, goodsTitle: goods.value.title } })
}
async function handleRelist() {
  await showConfirmDialog({ title: '确认上架', message: '重新上架后其他用户可看到该商品' })
  try { await changeGoodsStatus(goods.value.id, 'active'); goods.value.status = 'active'; showToast('已上架') } catch {}
}

onMounted(async () => {
  try {
    const res = await getGoodsDetail(route.params.id)
    goods.value = res.data
    if (userStore.isLoggedIn()) { const f = await isGoodsFavorited(route.params.id); favorited.value = f.data }
  } catch {}
})
</script>

<style scoped>
.detail-page { background: #f5f5f5; min-height: 100vh; }
.img-swipe { width: 100%; height: 300px; background: #000; }
.detail-img { width: 100%; height: 300px; object-fit: contain; }
.img-placeholder { width: 100%; height: 200px; background: #f5f5f5; display: flex; align-items: center; justify-content: center; }

.price-card { background: #fff; padding: 16px; margin-bottom: 8px; position: relative; }
.price-main { font-size: 28px; font-weight: 700; color: #ee0a24; }
.price-sub { display: flex; align-items: center; gap: 8px; margin-top: 4px; }
.original { font-size: 13px; color: #999; text-decoration: line-through; }
.status-tag { position: absolute; top: 16px; right: 16px; }

.section { background: #fff; padding: 16px; margin-bottom: 8px; }
.title-section h1 { font-size: 17px; line-height: 1.5; font-weight: 500; }
.meta-row { font-size: 12px; color: #999; margin-top: 8px; display: flex; gap: 6px; }

.seller-card { margin-bottom: 8px; }
.seller-top { display: flex; align-items: center; gap: 12px; }
.seller-avatar { width: 40px; height: 40px; border-radius: 50%; background: #f0f0f0; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.seller-name { font-size: 15px; font-weight: 500; }
.seller-contact { font-size: 12px; color: #999; margin-top: 2px; }

.desc-section { margin-bottom: 8px; }
.section-title { font-size: 15px; font-weight: 600; margin-bottom: 10px; color: #333; }
.desc-content { font-size: 14px; line-height: 1.8; color: #555; white-space: pre-wrap; }


.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: #fff; display: flex; align-items: center;
  height: 50px; padding: 0 12px;
  box-shadow: 0 -1px 6px rgba(0,0,0,0.06);
  z-index: 100;
  padding-bottom: env(safe-area-inset-bottom);
}
.bar-icons { display: flex; align-items: center; gap: 4px; flex-shrink: 0; }
.bar-icon {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  min-width: 44px; height: 44px; cursor: pointer;
}
.bar-icon label { font-size: 10px; color: #666; margin-top: 1px; }
.bar-btn-main { width: 120px; height: 38px; font-size: 15px; font-weight: 600; border-radius: 20px; flex-shrink: 0; }
.bar-btn-half { width: 90px; height: 38px; font-size: 13px; border-radius: 20px; flex-shrink: 0; margin-left: 8px; }
.bar-btn-half:first-of-type { margin-left: auto; }
.bar-status { flex: 1; text-align: center; font-size: 14px; color: #999; }

.pay-body { padding: 16px; }
.pay-item { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; font-size: 14px; }
.pay-label { color: #999; }
.pay-price { font-size: 20px; font-weight: 700; color: #ee0a24; }
.pay-methods { margin: 12px 0; }
.qr-placeholder { text-align: center; padding: 16px; background: #f7f8fa; border-radius: 8px; }
.qr-placeholder p { margin-top: 6px; font-size: 12px; color: #666; }
.qr-tip { font-size: 10px !important; color: #bbb !important; }
</style>
